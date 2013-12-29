package com.ferguss;

import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.shape.impl.RectangleImpl;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class HelloService {

	@Autowired
	private ESTemplate esTemplate;

	public void createIndex() {
		if(esTemplate.getClient().admin().indices().exists(new IndicesExistsRequest("test")).actionGet().isExists()) {
			Assert.isTrue(esTemplate.getClient().admin().indices().delete(new DeleteIndexRequest("test")).actionGet().isAcknowledged());
		}
		Assert.isTrue(esTemplate.getClient().admin().indices().create(new CreateIndexRequest("test")).actionGet().isAcknowledged());
		Assert.isTrue(esTemplate.getClient().admin().indices().preparePutMapping("test").setType("test").setSource(getMapping(Email.class)).execute().actionGet().isAcknowledged());
	}

	private String getMapping(Class<Email> testClass) {
		try {
			XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
					.startObject()
						.startObject("test")
							.startObject("properties");

								xContentBuilder.startObject("geoshape")
									.field("type", "geo_shape")
								.endObject();

							xContentBuilder.endObject()
						.endObject()
					.endObject();

			return xContentBuilder.string();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void indexSomeTests() throws IOException {
		Random random = new Random();
		ObjectMapper objectMapper = new ObjectMapper();
		BulkRequestBuilder bulkRequestBuilder = new BulkRequestBuilder(esTemplate.getClient());
		for(int i=0; i<250000; i++) {
			try {
				Email email = new Email();
				email.setEmailAddress(random.nextLong() + "@gmail.com");
				if(random.nextDouble() < 0.3) {
					email.setGeoshape(getRandomPolygon(random));
				} else {
					email.setGeoshape(getRandomGeoPoint(random));
				}
				bulkRequestBuilder.add(new IndexRequest("email", "email").source(objectMapper.writeValueAsBytes(email)));
				if(i % 10000 == 0 && i > 0) {
					System.out.println(bulkRequestBuilder.execute().actionGet().hasFailures());
					System.out.println(i);
					bulkRequestBuilder = new BulkRequestBuilder(esTemplate.getClient());
				}
			} catch (Exception e) {
				i--;
			}
		}
		System.out.println(bulkRequestBuilder.execute().actionGet().hasFailures());
	}

	private Double getRandomDouble(Random random) {
		return 0 + Math.abs(random.nextDouble()*100 % 51);
	}

	private GeoPolygon getRandomPolygon(Random random) {
		List<List<Double[]>> coordinates = new ArrayList<>();
		coordinates.add(new ArrayList<Double[]>());
		for(int i=0; i<5; i++) {
			coordinates.get(0).add(new Double[]{getRandomDouble(random), getRandomDouble(random)});
		}
		coordinates.get(0).add(coordinates.get(0).get(0));
		GeoPolygon geoPolygon = new GeoPolygon(coordinates);

		List<Coordinate> coordinateList = new ArrayList<>();
		for(Double[] coords : geoPolygon.getCoordinates().get(0)) {
			coordinateList.add(new Coordinate(coords[0], coords[1]));
		}
		Polygon polygon = new Polygon(new LinearRing(new CoordinateArraySequence(coordinateList.toArray(new Coordinate[coordinateList.size()])),new GeometryFactory()),new LinearRing[]{}, new GeometryFactory());
		if(!polygon.isValid()) {
			throw new RuntimeException("grey");
		}
		return geoPolygon;
	}

	private GeoPoint getRandomGeoPoint(Random random) {
		Double[] coordinates = new Double[]{getRandomDouble(random), getRandomDouble(random)};
		return new GeoPoint(coordinates);
	}

	public int search() {
		QueryBuilder queryBuilder = QueryBuilders.geoShapeQuery("geoshape", new RectangleImpl(-5, 5, -5, 5, SpatialContext.GEO));
		return esTemplate.getClient().prepareSearch("test").setQuery(queryBuilder).execute().actionGet().getHits().hits().length;
	}
}
