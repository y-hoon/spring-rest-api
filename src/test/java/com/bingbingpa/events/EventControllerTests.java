package com.bingbingpa.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import com.bingbingpa.accounts.Account;
import com.bingbingpa.accounts.AccountRepository;
import com.bingbingpa.accounts.AccountRole;
import com.bingbingpa.accounts.AccountService;
import com.bingbingpa.commns.AppProperties;
import com.bingbingpa.commns.TestDescription;
import com.bingbingpa.common.BaseControllerTest;

public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;
    
    @Autowired
    AccountService accountService;
    
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    AppProperties appProperties;
    
    /**
     * 단일 테스트에 대해서는 상관없지만 여러 테스트를 할경우 토큰을 받아올때 계속해서 동일 유저를 저장하므로 
     * 에러가 발생한다. 
     * 가장 쉬운 방법은 id를 랜덤하게 생성해서 사용하거나 테스트를 수행할때 db를 지워준다. 
     */
    @Before
    public void Setup() {
    	this.eventRepository.deleteAll();
    	this.accountRepository.deleteAll();
    }
    
    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
    	EventDto event = EventDto.builder()
    			.name("Spring")
    			.description("Rest API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 7, 22, 39))
    			.closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 8, 22, 39))
                .beginEventDateTime(LocalDateTime.of(2019, 12, 7, 22, 39))
    			.endEventDateTime(LocalDateTime.of(2019, 12, 9, 22, 39))
    			.basePrice(100)
    			.maxPrice(200)
    			.limitOfEnrollment(100)
    			.location("가산디지털단지")
                .build();
        
    	mockMvc.perform(post("/api/events/")
    			.header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
        		.content(objectMapper.writeValueAsString(event)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION)) 
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("free").value(false))
            .andExpect(jsonPath("offline").value(true))
//            .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT))
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.query-events").exists())
            .andExpect(jsonPath("_links.update-event").exists())
            .andDo(document("create-event",
            		links(
            				linkWithRel("self").description("link to self"),
            				linkWithRel("query-events").description("link to query events"),
            				linkWithRel("update-event").description("link to update an existing event"),
            				linkWithRel("profile").description("link to update an existing event")
            		),
            		requestHeaders(
            				headerWithName(HttpHeaders.ACCEPT).description("accept header"),
            				headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
    				),
            		requestFields(
            				fieldWithPath("name").description("Name of new event"),
            				fieldWithPath("description").description("description of new event"),
            				fieldWithPath("beginEnrollmentDateTime").description("begin enroll time"),
            				fieldWithPath("closeEnrollmentDateTime").description("close enroll time"),
            				fieldWithPath("beginEventDateTime").description("begin event time"),
            				fieldWithPath("endEventDateTime").description("end event time"),
            				fieldWithPath("location").description("location of new event"),
            				fieldWithPath("basePrice").description("base price of new event"),
            				fieldWithPath("maxPrice").description("max price of new event"),
            				fieldWithPath("limitOfEnrollment").description("limit of enrollment")
    				),
            		responseHeaders(
            				headerWithName(HttpHeaders.LOCATION).description("Location header"),
                            headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
    				),
            		/**
            		 * relaxedResponseFields 를 쓰면 모든 필드를 기술할 필요가 없다. 
            		 * 하지만 모든 필드를 기술하지 않으므로 정확한 문서를 만들지 못한다. 
            		 * responseFields를 쓰면 모든 필드를 기술해야 한다. 
            		 */
            		relaxedResponseFields(
            				fieldWithPath("id").description("Identifier of new event"),
            				fieldWithPath("name").description("Name of new event"),
            				fieldWithPath("description").description("description of new event"),
            				fieldWithPath("beginEnrollmentDateTime").description("begin enroll time"),
            				fieldWithPath("closeEnrollmentDateTime").description("close enroll time"),
            				fieldWithPath("beginEventDateTime").description("begin event time"),
            				fieldWithPath("endEventDateTime").description("end event time"),
            				fieldWithPath("location").description("location of new event"),
            				fieldWithPath("basePrice").description("base price of new event"),
            				fieldWithPath("maxPrice").description("max price of new event"),
            				fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
            				fieldWithPath("free").description("it tells if this event is free or not"),
            				fieldWithPath("offline").description("it tells if this event is offline or not"),
            				fieldWithPath("eventStatus").description("event status"),
            				fieldWithPath("_links.self.href").description("link to self"),
                            fieldWithPath("_links.query-events.href").description("link to query event list"),
                            fieldWithPath("_links.update-event.href").description("link to update existing event"),
                            fieldWithPath("_links.profile.href").description("link to profile")
    				)
            ));
    }
    
    @Test
    @TestDescription("정상적으로 값을 입력 받을 수 없는 경우 테스트")
    public void createEvent_Bad_Request() throws Exception {
    	Event event = Event.builder()
    			.id(100)
    			.name("Spring")
    			.description("Rest API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 7, 22, 39))
    			.closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 8, 22, 39))
                .beginEventDateTime(LocalDateTime.of(2019, 12, 7, 22, 39))
    			.endEventDateTime(LocalDateTime.of(2019, 12, 9, 22, 39))
    			.basePrice(100)
    			.maxPrice(200)
    			.limitOfEnrollment(100)
    			.location("가산디질털단지")
    			.free(true)
    			.offline(false)
    			.eventStatus(EventStatus.PUBLISHED)
                .build();
        
    	mockMvc.perform(post("/api/events/")
    			.header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
        		.content(objectMapper.writeValueAsString(event)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }
    
    @Test
    @TestDescription("빈값이 들어오는 경우 테스트 ")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
    	EventDto eventDto = EventDto.builder().build();
    	
    	this.mockMvc.perform(post("/api/events")
    				.header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
    				.contentType(MediaType.APPLICATION_JSON)
    				.content(objectMapper.writeValueAsString(eventDto)))
    			.andExpect(status().isBadRequest());
    }
    
    @Test
    @TestDescription("잘못된 값이 입력되는 경우 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
    	EventDto eventDto = EventDto.builder()
    			.name("Spring")
    			.description("Rest API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 17, 22, 39))
    			.closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 8, 22, 39))
                .beginEventDateTime(LocalDateTime.of(2019, 12, 7, 12, 39))
    			.endEventDateTime(LocalDateTime.of(2019, 12, 9, 22, 39))
    			.basePrice(10000)
    			.maxPrice(200)
    			.limitOfEnrollment(100)
    			.location("gasan")
    			.build();
    	
    	this.mockMvc.perform(post("/api/events")
    				.header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
    				.contentType(MediaType.APPLICATION_JSON)
    				.content(objectMapper.writeValueAsString(eventDto)))
    			.andDo(print())
    			.andExpect(status().isBadRequest())
    			.andExpect(jsonPath("content[0].objectName").exists())
    			.andExpect(jsonPath("content[0].defaultMessage").exists())
    			.andExpect(jsonPath("content[0].code").exists())
    			.andExpect(jsonPath("_links.index").exists())
    			;
    }
    
    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
    	// Given
    	IntStream.range(0, 30).forEach(this::generateEvent);
    	
    	// when
    	this.mockMvc.perform(get("/api/events")
    				.param("page", "1")
    				.param("size", "10")
    				.param("sort", "name,DESC"))
    			.andDo(print())
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("page").exists())
    			.andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
    			.andExpect(jsonPath("_links.self").exists())
    			.andExpect(jsonPath("_links.profile").exists())
    			.andDo(document("query-events"))
    			;
    }
    
    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 인증정보 가지고 조회하기")
    public void queryEventsWithAuthetication() throws Exception {
    	// Given
    	IntStream.range(0, 30).forEach(this::generateEvent);
    	
    	// when
    	this.mockMvc.perform(get("/api/events")
    				.header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
    				.param("page", "1")
    				.param("size", "10")
    				.param("sort", "name,DESC"))
    			.andDo(print())
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("page").exists())
    			.andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
    			.andExpect(jsonPath("_links.self").exists())
    			.andExpect(jsonPath("_links.profile").exists())
    			.andExpect(jsonPath("_links.create-event").exists())
    			.andDo(document("query-events"))
    			;
    }
    
    @Test
    @DisplayName("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
    	// Given
    	Account account = this.createAccount();
    	Event event = this.generateEvent(100, account);
    	
    	// When & Then 
    	this.mockMvc.perform(get("/api/events/{id}", event.getId()))
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("name").exists())
    			.andExpect(jsonPath("id").exists())
    			.andExpect(jsonPath("_links.self").exists())
    			.andExpect(jsonPath("_links.profile").exists())
    			.andDo(document("get-an-event"))
    			;
    }
    
    @Test
    @DisplayName("없는 이벤트 조회했을 때 404 응답받기")
    public void getEvnet404() throws Exception {
    	// When & Then 
    	this.mockMvc.perform(get("/api/event/11313"))
    			.andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
    	// Given
    	Account account = this.createAccount();
    	Event event = this.generateEvent(200, account);
    	String eventName = "Updated Event";
    	EventDto eventDto = this.modelMapper.map(event, EventDto.class);
    	eventDto.setName(eventName);
    	
    	// When & Then
    	this.mockMvc.perform(put("/api/events/{id}", event.getId())
    					.header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
    					.contentType(MediaType.APPLICATION_JSON)
    					.content(this.objectMapper.writeValueAsString(eventDto)))
    			.andDo(print())
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("name").value(eventName))
    			.andExpect(jsonPath("_links.self").exists())
    			.andDo(document("update-event"));
    }
    
    @Test
    @DisplayName("입력값이 비어있는 경우에 이벤트 수정 실패")
    public void updateEvent400_Empty() throws Exception {
    	// Given
    	Event event = this.generateEvent(200);
    	
    	EventDto eventDto = new EventDto();
    	
    	// When & Then
    	this.mockMvc.perform(put("/api/events/{id}", event.getId())
    				.header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
	    			.contentType(MediaType.APPLICATION_JSON)
					.content(this.objectMapper.writeValueAsString(eventDto)))
    			.andDo(print())
    			.andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("입력값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEvent400_Wrong() throws Exception {
    	// Given
    	Event event = this.generateEvent(200);
    	
    	EventDto eventDto = this.modelMapper.map(event, EventDto.class);
    	eventDto.setBasePrice(20000);
    	eventDto.setMaxPrice(1000);
    	
    	// When & Then
    	this.mockMvc.perform(put("/api/events/{id}", event.getId())
    				.header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
	    			.contentType(MediaType.APPLICATION_JSON)
					.content(this.objectMapper.writeValueAsString(eventDto)))
    			.andDo(print())
    			.andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("존재하지 않는 이벤트 수정 실패")
    public void updateEvent404() throws Exception {
    	// Given
    	Event event = this.generateEvent(200);
    	EventDto eventDto = this.modelMapper.map(event, EventDto.class);
    	
    	// When & Then
    	this.mockMvc.perform(put("/api/events/313442")
    				.header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
	    			.contentType(MediaType.APPLICATION_JSON)
					.content(this.objectMapper.writeValueAsString(eventDto)))
    			.andDo(print())
    			.andExpect(status().isNotFound());
    }
    
    private Event generateEvent(int index, Account account) {
    	Event event = buildEvent(index);
    	event.setManger(account);
    	return this.eventRepository.save(event);
    }
    
    private Event generateEvent(int index) {
    	Event event = buildEvent(index);
    	return this.eventRepository.save(event);
    }
    
    private Event buildEvent(int index) {
    	return Event.builder()
    			.name("event" + index)
    			.description("Rest API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 7, 22, 39))
    			.closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 8, 22, 39))
                .beginEventDateTime(LocalDateTime.of(2019, 12, 7, 22, 39))
    			.endEventDateTime(LocalDateTime.of(2019, 12, 9, 22, 39))
    			.basePrice(100)
    			.maxPrice(200)
    			.limitOfEnrollment(100)
    			.location("가산디지털단지")
    			.free(false)
    			.offline(true)
    			.eventStatus(EventStatus.DRAFT)
    			.build();
    }
    
    private String getBearerToken(boolean needToCreateAccount) throws Exception {
        return "Bearer " + getAccessToken(needToCreateAccount);
    }
    
    private String getAccessToken(boolean needToCreateAccount) throws Exception {
		// Given
    	if (needToCreateAccount) {
    		createAccount();
    	}
    	
		ResultActions perform = this.mockMvc.perform(post("/oauth/token")
					.with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
					.param("username", appProperties.getUserUsername())
					.param("password", appProperties.getUserPassword())
					.param("grant_type",  "password"));
		String responseBody = perform.andReturn().getResponse().getContentAsString();
		Jackson2JsonParser parser = new Jackson2JsonParser();
		
		return parser.parseMap(responseBody).get("access_token").toString();
	}
    
    private Account createAccount() {
    	Account account = Account.builder()
				.email(appProperties.getUserUsername())
				.password(appProperties.getUserPassword())
				.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
				.build();
		return this.accountService.saveAccount(account);
    }
}
