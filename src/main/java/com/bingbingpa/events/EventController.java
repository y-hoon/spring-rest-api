package com.bingbingpa.events;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private EventRepository eventRepository;
    private final ModelMapper modelMapper;
    
    public EventController(EventRepository eventRepository, ModelMapper modelMapper) {
    	this.eventRepository = eventRepository;
    	this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody EventDto eventDto) {
    	Event event = modelMapper.map(eventDto, Event.class);
        Event newEvent = this.eventRepository.save(event);
        URI createUri = linkTo(EventController.class).toUri();
        eventDto.setId(10);
        return ResponseEntity.created(createUri).body(event);
    }
}