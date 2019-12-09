package com.bingbingpa.events;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    
    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
    	this.eventRepository = eventRepository;
    	this.modelMapper = modelMapper;
    	this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
    	if(errors.hasErrors()) {
    		return ResponseEntity.badRequest().build();
    	}
    	
    	eventValidator.validate(eventDto, errors);
    	if(errors.hasErrors()) {
    		return ResponseEntity.badRequest().build();
    	}
    	Event event = modelMapper.map(eventDto, Event.class);
        Event newEvent = this.eventRepository.save(event);
        URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
//        eventDto.setId(10);
        return ResponseEntity.created(createUri).body(event);
    }
}