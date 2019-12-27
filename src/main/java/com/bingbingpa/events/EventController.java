package com.bingbingpa.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.util.Optional;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bingbingpa.accounts.Account;
import com.bingbingpa.accounts.CurrentUser;
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
    public ResponseEntity<?> createEvent(@RequestBody @Valid EventDto eventDto, Errors errors, @CurrentUser Account account) {
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
        event.setManger(account); //이벤트의 매니저 설정 
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
	public ResponseEntity<?> queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler,
			@CurrentUser Account account) {
//    	Authentication authenticateAction = SecurityContextHolder.getContext().getAuthentication();
//    	User principaUser = (User)authenticateAction.getPrincipal();
    	
    	Page<Event> page = this.eventRepository.findAll(pageable);
    	PagedModel<EntityModel<Event>> pageResource = assembler.toModel(page, e -> new EventResource(e));
    	pageResource.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
    	// 사용자 정보가 있을 경우에만 create-event링크 노출 
    	if (account != null) {
    		pageResource.add(linkTo(EventController.class).withRel("create-event"));
    	}
    	return ResponseEntity.ok(pageResource);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Integer id, @CurrentUser Account account) {
    	Optional<Event> optionalEvent = this.eventRepository.findById(id);
    	if (optionalEvent.isEmpty()) {
    		return ResponseEntity.notFound().build();
    	}
    	
    	Event event = optionalEvent.get();
    	EventResource eventResource = new EventResource(event);
    	eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
    	// 매니저인 경우에만 업데이트 이벤트 링크 노출 
    	if (event.getManger().equals(account)) {
    		eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
    	}
    	return ResponseEntity.ok(eventResource);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updatEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto,
    									Errors errors, @CurrentUser Account account) {
    	Optional<Event> optionalEvent = this.eventRepository.findById(id);
    	if (optionalEvent.isEmpty()) {
    		return ResponseEntity.notFound().build();
    	}
    	
    	if (errors.hasErrors()) {
    		return badRequest(errors);
    	}
    	
    	this.eventValidator.validate(eventDto, errors);
    	if (errors.hasErrors()) {
    		return badRequest(errors);
    	}
    	
    	Event existingEvent = optionalEvent.get();
    	if(!existingEvent.getManger().equals(account)) {
    		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    	}
    	this.modelMapper.map(eventDto, existingEvent);
    	Event savedEvent = this.eventRepository.save(existingEvent);
    	
    	EventResource eventResource = new EventResource(savedEvent);
    	eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));
    	
    	return ResponseEntity.ok(eventResource);
    }
    
    private ResponseEntity<?> badRequest(Errors errors) {
    	return ResponseEntity.badRequest().body(new ErrorResource(errors));	
    			
    }
}