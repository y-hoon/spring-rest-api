package com.bingbingpa.events;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EventRepository eventRepository;

    @Ignore
    public void createEvent() throws Exception {
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
                .build();
        
    	mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
        		.content(objectMapper.writeValueAsString(event)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE));
    }
    
    @Test
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
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
        		.content(objectMapper.writeValueAsString(event)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }
}