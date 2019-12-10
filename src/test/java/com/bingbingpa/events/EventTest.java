package com.bingbingpa.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder().build();
        assertThat(event).isNotNull();
    }

    @Test
    public void JavaBean() {
        // Given
        String name = "Event";
        String description = "Spring";

        // When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        
        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }
    
    /**
     * @parameter(method = "parametersForTestFree") 이렇게 명시적으로 적어줘도 되고 아니면 
     * parametersFor라는 prefix를 붙이면 찾아서 파라미터로 사용한다. 
     */
    @Test
    @Parameters
    public void testFree(int basePrice, int maxPrice, boolean isFree) {
    	// given
    	Event event = Event.builder()
    				.basePrice(basePrice)
    				.maxPrice(maxPrice)
    				.build();
    	// when
    	event.update();
    	
    	// then
    	assertThat(event.isFree()).isEqualTo(isFree);    
	}
    
    private Object[] parametersForTestFree() {
    	return new Object[] {
    		new Object[] {0, 0, true},
    		new Object[] {100, 0, false},
    		new Object[] {0, 100, false},
    		new Object[] {100, 200, false}
    	};
    }
    
    @Test
    @Parameters(method = "paramsTestOffline")
    public void testOffline(String location, boolean isOffline) {
    	// given
    	Event event = Event.builder()
    				.location(location)
    				.build();
    	// when
    	event.update();
    	
    	// then
    	assertThat(event.isOffline()).isEqualTo(isOffline);
    }
    
    private Object[] paramsTestOffline() {
    	return new Object[] {
    		new Object[] {"gasan", true},
    		new Object[] {"", false}
    	};
    }
}