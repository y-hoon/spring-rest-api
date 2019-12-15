package com.bingbingpa.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

public class EventResource extends EntityModel<Event> {
	
	/*
	 * self link는 resource마다 설정해줘야 하므로 여기에 공통으로 추가한다.
	 */
	public EventResource(Event event, Link... links) {
		super(event,  links);
		add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
	}
}
