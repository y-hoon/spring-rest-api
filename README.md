##spring-rest-api

- REST의 제약조건 중에서 특히 Self-descriptive와 HATEOAS를 잘 만족하지 못한다.
- REST는 긴 시간에 걸쳐 진화하는 웹 애플리케이션을 위한 것이다. 
- Self-descriptive는 custom media type이나 profile link relation 등으로 만족시킬 수 있다.
- HATEOAS는 HTTP헤더나 본문에 링크를 담아 만족시킬 수 있다.

##gradle 의존성 옵션들 

- implementation : 의존 라이브러리 수정시 본 모듈까지만 재빌드
- api : 의존 라이브러리 수정시 본 모듈을 의존하는 모듈들도 재빌드
- compileOnly : comple 시에만 빌드하고  빌드 결과물에 포함하지 않는다. runtime 시 필요없는 라이브러리인 경우(runtime 환경에 이미 라이브러리가 제공되고 있는 경우)
- runtimeOnly : runtime 시에만 필요한 라이브러리인 경우
- annotationProcessor : 컴파일 시점으로 코드를 생성함. 이것을 설정안하면 롬복을 포함해서 프로젝트를 export할 때 롬복에서 제공되는 에노테이션이 전부 포함되지 않음

##h2와 postgres 설정 분리하기 

- 어플리케이션을 구동할때는 postgres를 테스트 코드를 실행할때는 h2 를 사용하기 위한 설정. 
- test폴더 하위에 resource폴더를 만들고 application.properties를 복사하고 이름을 **application-test.properties**로 변경한다.
- 변경 후 h2의 datasource 내용만 추가한다. 나머지 부분은 기존의 application.properties의 내용을 가져다 쓰게 된다. 
- 테스트 코드에서는 @ActiveProfiles("test") 라는 어노테이션을 추가해서 해당 프로퍼티(h2)를 사용한다고 선언해준다. 
- 프로젝트의 build path에 test에 추가한 resoucre폴더를 추가해준다. 
- 어플리케이션을 실행할때와 테스트 코드를 실행했을때 콘솔에서 **dialect**를 검색해서 설정한 propertie의 db가 맞는지 확인한다.

##스프링 시큐리티 

- 스프링 시큐리티에는 웹시큐리티와 메소드 시큐리티가 존재한다. 
- 웹 시큐리티는 웹 요청에 대한 filter기반 시큐리티
- 메소드 시큐리티는 웹과 상관없이 어떤 메소드가 호출 되었을때 적용하는 시큐리티  


