package com.ferguss;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.internal.InternalNode;
import org.springframework.stereotype.Component;

@Component
public class ESTemplate {

	private Client client;
	private Node node;

	public ESTemplate() {
		this.node = new InternalNode();
		node.start();
		this.client = node.client();
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
}
