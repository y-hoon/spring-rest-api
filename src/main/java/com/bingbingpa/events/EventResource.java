package com.bingbingpa.events;

import org.springframework.hateoas.EntityModel;

public class EventResource extends EntityModel<Event> {
	
	private Event event;
	
	public EventResource(Event event) {
		this.event = event;
	}
	
	public Event getEvent() {
		return event;
	}
}
