package com.bingbingpa.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.bingbingpa.commns.TestDescription;
import com.bingbingpa.common.RestdocsConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestdocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    EventRepository eventRepository;

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
            		responseFields(
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
    
    private void generateEvent(int index) {
    	Event event = Event.builder()
    			.name("event" + index)
    			.description("test event")
    			.build();
    	this.eventRepository.save(event);
    }
}













