package com.bingbingpa.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bingbingpa.commns.ErrorResource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    public ResponseEntity<?> createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
    	/**
    	 * Event 도메인은 java Bean스펙을 준수하고 있기 때문에 serializable할 수 있지만 에러 객체는 할 수 없다.
    	 */
    	if(errors.hasErrors()) {
    		return badRequest(errors);
    	}
    	
    	eventValidator.validate(eventDto, errors);
    	if(errors.hasErrors()) {
    		return badRequest(errors);
    	}
    	Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = this.eventRepository.save(event);
        
        // hateoas 링크 추가
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        log.info("eventResource ========================== {} " , eventResource);
        return ResponseEntity.created(createdUri).body(eventResource);
    }
    
    @GetMapping
    public ResponseEntity<?> queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
    	Page<Event> page = this.eventRepository.findAll(pageable);
    	PagedModel<EntityModel<Event>> pageResource = assembler.toModel(page, e -> new EventResource(e));
    	pageResource.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
    	return ResponseEntity.ok(pageResource);
    }
    private ResponseEntity<?> badRequest(Errors errors) {
    	return ResponseEntity.badRequest().body(new ErrorResource(errors));	
    			
    }
}