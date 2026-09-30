week3 주요 주제 : DDD 4계층과 DIP 구현

JPA(Java Persistence API) : 자바 애플리케이션에서 객체와 Relational DB 테이블 간의 차이를 줄이고, SQL을 직접 작성하지 않고도 DB를 다룰 수 있게 해주는 자바의 표준 ORM(Object-Relational Mapping) 기술 규격
JPA는 개념이자 interface들의 모음이며, 이를 실제 구현한 대표적인 프레임워크가 Hibernate임
Spring Data JPA : JPA를 스프링 프레임워크 상에서 훨씬 더 쉽고 편하게 사용할 수 있도록 추상화한 모듈

즉, 정리하자면
JPA : 자바 ORM 기술 표준 (규격) ex) @Id, @Entity 등
Hibernate : JPA 구현체 (실제 작동하는 엔진)
Spring Data JPA : JPA를 스프링에서 더 생산성 높게 쓰기 위한 개발용 라이브러리/프레임워크

DDD 4계층 구성 -> presentation(바깥에서 들어오는 요청을 받는다), application(명령 순서를 정한다, 구현(책임)은 없음), domain(규칙을 지킨다, 구현한다), infrastructure(기술을 이용해 실제로 처리한다)
4계층으로 구분 시 장점 : “위치”를 묶어 의존성을 확인하고, DIP를 구현하기 위함
SOLID : 객체 지향 프로그래밍 및 설계에서 소프트웨어를 더 이해하기 쉽고, 유연하며, 유지보수하기 쉽게 만들기 위한 5가지 핵심 원칙의 앞 글자를 딴 약어
SRP(Single Responsibility, 단일 책임 원칙) : 하나의 클래스는 하나의 책임(기능)만 맡아야함
OCP(Open-Closed Principle, 개방-폐쇄 원칙) : 기존의 코드를 변경하지 않으면서(Closed), 새로운 기능이나 클래스를 추가(Open)할 수 있어야 함
LSP(Liskov Substitution Principle, 리스코프 치환 원칙) : 부모 클래스의 규약(의도)을 자식 클래스가 위반해서는 안 됨
ISP(Interface Segregation Principle, 인터페이스 분리 원칙) : 지나치게 거대한 범용 인터페이스 하나보다, 작고 구체적인 인터페이스 여러 개가 나음
DIP(Dependency Inversion Principle, 의존역전 원칙) : 고수준 모듈(비즈니스 로직)은 저수준 모듈(구체적인 기술)의 변경에 영향을 받지 않아야 함

Spring Data JPA의 Repository 인터페이스를 서비스 로직에서 주입받아 쓰는 것이 DIP의 대표적인 예시

기존 구조의 문제점 : DIP를 만족하지 못하여 infrastructure가 변하면 application도 수정되어야 
-> DIP를 적용함으로써 Application과 Infrastructure 사이의 직접적인 연결을 끊어내는 것이 week3의 중점

구현체인 Adapter(Infrastructure)가 추상화된 ProductRepository(Domain)를 향해 의존하도록 만들어, 기술 변경이 도메인 영역으로 전파되지 않도록 막아야함
추구하는 결과 : Domain 및 Application 영역은 프레임워크나 외부 기술 종속성이 없는 순수 Java 코드(POJO)로 유지, ProductService(Application)는 기술(JPA)을 모르고 ProductRepository(Port)만 바라보며, 기술(JPA/Adapter)이 명세에 맞추어 끼워지는 부품 형태가 됨으로써 완벽한 의존성 역전(DIP)과 도메인 순수성을 달성한 구조
코드 수정 확인 : Fake test로써 기존 JPA 대신 HashMap 이용하여 확인 가능

**findById에서 Optional<>을 사용하는 이유** : 조회 결과가 존재하지 않을 수 있음(null)을 타입 시스템 차원에서 명시적으로 표현하기 위함