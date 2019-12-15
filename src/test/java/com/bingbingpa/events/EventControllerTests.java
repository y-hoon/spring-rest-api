package com.bingbingpa.events;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.bingbingpa.commns.TestDescription;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.RestdocsConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestdocsConfiguration.class)
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    ObjectMapper objectMapper;

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
            .andDo(document("create-event"))
            ;
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
    			.andExpect(jsonPath("$[0].objectName").exists())
    			.andExpect(jsonPath("$[0].defaultMessage").exists())
    			.andExpect(jsonPath("$[0].code").exists());
    }
}