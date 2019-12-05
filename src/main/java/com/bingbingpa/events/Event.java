package com.bingbingpa.events;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * EqualsAndHashCode를 쓸때는 기본적으로 모든 필드를 가지고 하는데 만약에 다른 클래스와 연관 관계가 있을 경우 상호 참조 때문에
 * 스택오버 플로우 에러가 발생 할 수 있다. 그래서 고유한 ID나 몇개의 필드로만 쓰는게 좋다. 
 * 엔티티에는 @Data를 쓰지 않는다. 역시 상호 참조 때문에 오류가 날 수 있다.
 */
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {
    
    private String name;
    private Integer id;
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
    private EventStatus eventStatus;

}

