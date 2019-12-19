## gradle 환경에서 asciidoctor 사용하기

- adoc파일 조각들에 정의한 index.adoc파일의 위치는 **src/main/docs/asciidoc**이 아니라 **src/docs/asciidoc**이 되어야 한다. 
- 만약 해당 경로가 맞지 않다면 html파일이 만들어 지지 않는다. 
- 정상적으로 빌드가 되었다면 build/asciidoc/html5 경로에 adoc파일 이름과 동일한 html 파일이 만들어 진다. 
- 만약 html 파일이 만들어 지지 않는다면 디버깅해서 발생하는 에러를 확인 한다. **gradle asciidoctor -d**
- TODO :index.adoc에 경로가 맞지 않는 문제 수정하기(기능 구현 다 끝나고...) 
