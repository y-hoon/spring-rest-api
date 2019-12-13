package com.bingbingpa.events;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * EqualsAndHashCode를 쓸때는 기본적으로 모든 필드를 가지고 하는데 만약에 다른 클래스와 연관 관계가 있을 경우 상호 참조 때문에
 * 스택오버 플로우 에러가 발생 할 수 있다. 그래서 고유한 ID나 몇개의 필드로만 쓰는게 좋다. 
 * 엔티티에는 @Data를 쓰지 않는다. 역시 상호 참조 때문에 오류가 날 수 있다.
 */
@Builder @AllArgsConstructor @NoArgsConstructor @ToString
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
public class Event {
    
	@Id @GeneratedValue
	private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;
    
    // 간단한 비즈니스로직은 도메인에서 처리하는 것도 나쁘지 않다. 
    // 또는 서비스에 위임해서 분리하도록 하는 것이 좋다. 
    public void update() {
    	// Update free
    	if (this.basePrice == 0  && this.maxPrice == 0) {
    		this.free =true;
    	} else {
    		this.free = false;
    	}
    	// isBlank는 자바 11에서 추가됨 
    	// Update offline 
    	if (this.location == null || this.location.isBlank()) {
    		this.offline = false;
    	} else {
    		this.offline = true;
    	}
    }
}

