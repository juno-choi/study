`출처` [인프런 Readable Code](https://www.inflearn.com/course/lecture?courseSlug=readable-code-%EC%9D%BD%EA%B8%B0%EC%A2%8B%EC%9D%80%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1%EC%82%AC%EA%B3%A0%EB%B2%95&unitId=207880&tab=curriculum)

# 🔴 SOLID 란

`SRP` Single Responsibility Principle  
`OCP` Open-Closed Principle  
`LSP` Liskov Substitution Principle  
`ISP` Interface Segregation Principle  
`DIP` Dependency Inversion Principle

다음 의미를 가지고 있다. 코드에 SOLID 원칙이 적용되는 예시를 하나씩 적어보려 한다.

## 🟠 SRP(Single Responsibility Principle)

단일 책임 원칙으로 하나의 class는 하나의 책임을 가진다는 것이다.

### 🟢 예시

```java
public class Main {
    public static void main(String[] args) {
        PocketMon 피카츄 = new PocketMon("피카츄", "electric", 10, 100);
        int 피카츄_공격 = 피카츄.attack();

        PocketMon 파이리 = new PocketMon("파이리", "fire", 10, 100);
        int 파이리_공격 = 파이리.attack();
    }
}

public class PocketMon {
    private String name;
    private String type;
    private int power;
    private int hp;

    public PocketMon(String name, String type, int power, int hp) {
        this.name = name;
        this.type = type;
        this.power = power;
        this.hp = hp;
    }

    public int attack() {
        int damage = power;
        if ("fire".equals(this.type)) {
            damage = (int) (power * 1.2);
        }
        System.out.println("%s(이/가) %d의 데미지를 주었습니다.".formatted(name, damage));
        return damage;
    }
}
```

다음 소스에서 피카츄와 파이리는 attack시 타입에 따라 공격력을 계산하여 반환해주는 로직이다. 여기서 SRP를 지키지 못한 곳은 어디일까?

PocketMon class는 현재 포켓몬을 생성하는 책임을 가지고 있는데 attack의 책임도 가지고 있다.

이 소스를 SRP에 따라 분리해보자.

### 🟢 수정 후 코드

```java
public class PocketMon {
    private String name;
    private String type;
    private int power;
    private int hp;

    public PocketMon(String name, String type, int power, int hp) {
        this.name = name;
        this.type = type;
        this.power = power;
        this.hp = hp;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getPower() {
        return power;
    }

    public int getHp() {
        return hp;
    }
}
```
먼저 PocketMon의 데이터를 가져다 쓰기 위해 getter를 만든다.

```java
public class Battle {
    public int attack(PocketMon pocketMon) {
        int damage = 0;

        damage = getDamage(pocketMon);

        System.out.println("%s(이/가) %d의 데미지를 주었습니다.".formatted(pocketMon.getName(), damage));
        return damage;
    }

    private static int getDamage(PocketMon pocketMon) {
        if ("fire".equals(pocketMon.getType())) {
            return (int) (pocketMon.getPower() * 1.2);
        }
        return pocketMon.getPower();
    }
}
```
Battle class를 추가하여 공격에 관련된 정의를 class에 넣는다.

```java
public class Main {
    public static void main(String[] args) {
        Battle battle = new Battle();

        PocketMon 피카츄 = new PocketMon("피카츄", "electric", 10, 100);
        int 피카츄_공격 = battle.attack(피카츄);

        PocketMon 파이리 = new PocketMon("파이리", "fire", 10, 100);
        int 파이리_공격 = battle.attack(파이리);
    }
}
```
그 후 main에 로직을 다음과 같이 정리해주면 포켓몬을 생성하는 class와 배틀에 관련한 class를 각 책임에 따라 분리할 수 있다.

## 🟠 OCP(Open-Closed Principle)

확장에는 열려있고, 수정에는 닫혀 있어야 한다.
기존 코드이 변경 없이, 시스템의 기능을 확장할 수 있어야 한다.

### 🟢 예시

위에 코드 그대로 예시로 봤을때 현재 OCP적으로 문제가 발생하는 부분은 Battle class의 attack 부분이다. 
그 이유는 getDamage()에서 전기 포켓몬의 공격력 계산식이 변경된다고 가정했을 때 불 포켓몬의 계산식이 함께 들어있는 getDamage()의 계산식을 변경해야 되기 때문이다.
이 문제를 OCP적으로 해결해보자.

```java
public interface BattleStrategy {
    int attack(PocketMon pocketMon);
}
```
먼저 BattleStrategy interface를 생성한다. 그리고 해당 interface를 상속하는 모든 class는 attack을 정의하도록 한다.

```java
public class ElectronicBattleStrategy implements BattleStrategy{
    @Override
    public int attack(PocketMon pocketMon) {
        return pocketMon.getPower();
    }
}

public class FireBattleStrategy implements BattleStrategy{

    @Override
    public int attack(PocketMon pocketMon) {
        return (int) (pocketMon.getPower() * 1.2);
    }
}
```
전기 타입과 불 타입의 배틀 전략을 생성해둔다.

```java
public class Battle {
    public int attack(PocketMon pocketMon) {
        int damage = 0;

        damage = getDamage(pocketMon);

        System.out.println("%s(이/가) %d의 데미지를 주었습니다.".formatted(pocketMon.getName(), damage));
        return damage;
    }

    private static int getDamage(PocketMon pocketMon) {
        BattleStrategy battleStrategy = null;
        if ("fire".equals(pocketMon.getType())) {
            battleStrategy = new FireBattleStrategy();
        } else if ("electric".equals(pocketMon.getType())) {
            battleStrategy = new ElectronicBattleStrategy();
        }

        if (battleStrategy == null) {
            throw new IllegalArgumentException("Unknown type: " + pocketMon.getType());
        }

        return battleStrategy.attack(pocketMon);
    }
}
```
그리고 getDamage의 경우 기존에 데미지를 계산하는 로직이 있던것에서 BattleStrategy를 어떤걸 가져올지로 로직을 변경한다.
이렇게 되면 기존 main에서는 코드가 전혀 변경되지 않았고 Battle class에서는 포켓몬의 타입에 따라 자동으로 계산로직이 들어가게 되었다.

여기서 OCP를 통해 분리된 소스를 보면 앞으로 포켓몬의 attack 계산 로직은 오롯이 타입에 따라 수정할 수 있게 되었고 
만약 새로운 포켓몬 타입을 추가한다면 BattleStrategy를 상속받은 새로운 class를 만들어서 진행하게되면 된다. 

## 🟠 LSP(Liskov Substitution Principle)

상속 구조에서, 부모 클래스의 인스턴스를 자식 클래스의 인스턴스로 치환할 수 있어야 한다.

자식 클래스는 부모 클래스의 책임을 준수하며, 부모 클래스의 행동을 변경하지 않아야 한다.

### 🟢 예시

불타입 포켓몬인 파이리의 공격력이 너무 강해 너프를 진행하려고 한다. 현재 공격력이 12인 파이리의 공격을 -11 너프하기로 하였다.

```java
public class FireBattleStrategy implements BattleStrategy{

    @Override
    public int attack(PocketMon pocketMon) {
        return (int) ((pocketMon.getPower() - 11) * 1.2);
    }
}
```
* 물론 잘못된 코드인것을 알지만 예시를 위해 일부러 틀린것이다.

![](https://velog.velcdn.com/images/ililil9482/post/a177a545-a9ea-4930-8638-8bbb84e036a9/image.png)

-11 데미지를 너프한 경우 파이리의 데미지가 음수가 나오게 되었다. 공격이라는 의미상 음수는 잘못된 형태로 이 버그를 고쳐보려 한다.

```java
public class Battle {
    public int attack(PocketMon pocketMon) {
        int damage = 0;

        damage = getDamage(pocketMon);

        System.out.println("%s(이/가) %d의 데미지를 주었습니다.".formatted(pocketMon.getName(), damage));
        return damage;
    }

    private static int getDamage(PocketMon pocketMon) {
        BattleStrategy battleStrategy = null;
        if ("fire".equals(pocketMon.getType())) {
            battleStrategy = new FireBattleStrategy();
        } else if ("electric".equals(pocketMon.getType())) {
            battleStrategy = new ElectronicBattleStrategy();
        }

        if (battleStrategy == null) {
            throw new IllegalArgumentException("Unknown type: " + pocketMon.getType());
        }

        if (battleStrategy instanceof FireBattleStrategy) {
            if (pocketMon.getPower() < 11) {
                return 1;
            }

        }

        return battleStrategy.attack(pocketMon);
    }
}
```

Battle class에서 FireBattleStrategy인 경우 데미지가 11보다 작거나 같은 경우에는 1로 반환하도록 했다.

![](https://velog.velcdn.com/images/ililil9482/post/bc70b7a4-5be7-4a0e-adc0-cea473e31e88/image.png)

그 결과 문제가 해결된것처럼 보인다.

하지만 이는 LSP를 위반한 경우이다. LSP에 맞게 고쳐보자.

### 🟢 수정 후 코드

```java
public class Battle {
    public int attack(PocketMon pocketMon) {
        int damage = 0;

        damage = getDamage(pocketMon);

        System.out.println("%s(이/가) %d의 데미지를 주었습니다.".formatted(pocketMon.getName(), damage));
        return damage;
    }

    private static int getDamage(PocketMon pocketMon) {
        BattleStrategy battleStrategy = null;
        if ("fire".equals(pocketMon.getType())) {
            battleStrategy = new FireBattleStrategy();
        } else if ("electric".equals(pocketMon.getType())) {
            battleStrategy = new ElectronicBattleStrategy();
        }

        if (battleStrategy == null) {
            throw new IllegalArgumentException("Unknown type: " + pocketMon.getType());
        }

        return battleStrategy.attack(pocketMon);
    }
}
```

기존의 Battle class는 이전 소스와 동일하게 각 상속받은 interface가 어떤 class인지 검사하는 로직을 삭제한다.

그 이유는 부모 class를 상속한 자식 class는 부모 class가 사용되는 곳에서는 똑같이 동작해야되는 책임이 있기 때문에 상속 받은 class 타입을 체크하는 것이 잘못 되었기 때문이다.

```java
public class FireBattleStrategy implements BattleStrategy{

    @Override
    public int attack(PocketMon pocketMon) {
        int damage = (int) ((pocketMon.getPower() - 11) * 1.2);
        return damage < 0 ? 1 : damage;
    }
}
```

그리고 FireBattleStrategy에 공격력에 대한 음수 처리를 직접 해준다. 이렇게 되면 부모 class가 동작하는 부분에 동일하게 작동하므로 문제가 발생하지 않게 된다.

## 🟠 ISP(Interface Segregation Principle)

인터페이스 분리 원칙으로 인터페이스를 상속하는 class는 자신이 사용하지 않는 인터페이스에 의존하면 안된다는 것이다.

### 🟢 예시

전설의 포켓몬을 추가하는 패치를 진행하려고 한다. 그리고 전설 포켓몬은 등장할때 특별 효과를 넣어주려고 한다.

패치를 진행해보자.

```java
public class Main {
    public static void main(String[] args) {
        Battle battle = new Battle();

        PocketMon 피카츄 = new PocketMon("피카츄", "electric", 10, 100);
        int 피카츄_공격 = battle.attack(피카츄);

        PocketMon 파이리 = new PocketMon("파이리", "fire", 10, 100);
        int 파이리_공격 = battle.attack(파이리);

        PocketMon 뮤츠 = new PocketMon("뮤츠", "legend", 20, 120);
        int 뮤츠_공격 = battle.attack(뮤츠);
    }
}
```

새롭게 전설 포켓몬 뮤츠를 추가했다.

```java
public class Battle {
    public int attack(PocketMon pocketMon) {
        int damage = 0;

        damage = getDamage(pocketMon);

        System.out.println("%s(이/가) %d의 데미지를 주었습니다.".formatted(pocketMon.getName(), damage));
        return damage;
    }

    private static int getDamage(PocketMon pocketMon) {
        BattleStrategy battleStrategy = null;
        if ("fire".equals(pocketMon.getType())) {
            battleStrategy = new FireBattleStrategy();
        } else if ("electric".equals(pocketMon.getType())) {
            battleStrategy = new ElectronicBattleStrategy();
        } else if ("legend".equals(pocketMon.getType())) {
            battleStrategy = new LegendBattleStrategy();
            battleStrategy.legendAppear();
        }

        if (battleStrategy == null) {
            throw new IllegalArgumentException("Unknown type: " + pocketMon.getType());
        }

        return battleStrategy.attack(pocketMon);
    }
}

```

전설 포켓몬은 등장 이펙트를 위해 legendAppear()를 추가해야한다.

```java
public interface BattleStrategy {
    int attack(PocketMon pocketMon);

    void legendAppear();
}
```
interface에 메서드를 추가하고

```java
public class LegendBattleStrategy implements BattleStrategy {
    @Override
    public int attack(PocketMon pocketMon) {
        return pocketMon.getPower();
    }

    @Override
    public void legendAppear() {
        System.out.println("!!!!!레전드 포켓몬 등장!!!!!");
    }
}
```
LegendBattleStrategy에 배틀 등장시 효과를 추가했다.

```java
public class ElectronicBattleStrategy implements BattleStrategy{
    @Override
    public int attack(PocketMon pocketMon) {
        return pocketMon.getPower();
    }

    @Override
    public void legendAppear() {
        return ;
    }
}

public class FireBattleStrategy implements BattleStrategy{

    @Override
    public int attack(PocketMon pocketMon) {
        int damage = (int) ((pocketMon.getPower() - 11) * 1.2);
        return damage < 0 ? 1 : damage;
    }

    @Override
    public void legendAppear() {
        return ;
    }
}

```

여기서 우리는 LIP를 위반했다. 전설 포켓몬의 legendAppear()을 위해 BattleStrategy interface에 메서드를 추가했고 
그 결과 Electronic과 Fire에 모두 추가되어 사용하지 않는 메서드를 구현하고 있기 때문이다.

### 🟢 수정 후 코드

LIP를 위반하지 않도록 수정해보자.

```java
public interface BattleStrategy {
    int attack(PocketMon pocketMon);

}

public interface BattleStrategyForLegend {
    void legendAppear();

}
```
인터페이스를 일반 포켓몬과 레전드 포켓몬 용으로 나누었다.

```java
public class ElectronicBattleStrategy implements BattleStrategy{
    @Override
    public int attack(PocketMon pocketMon) {
        return pocketMon.getPower();
    }

}

public class FireBattleStrategy implements BattleStrategy{

    @Override
    public int attack(PocketMon pocketMon) {
        int damage = (int) ((pocketMon.getPower() - 11) * 1.2);
        return damage < 0 ? 1 : damage;
    }

}

```
일반 포켓몬들은 다시 필요 없는 legendAppear() 메서드를 지울 수 있게 되었다.

```java
public class LegendBattleStrategy implements BattleStrategy, BattleStrategyForLegend {
    @Override
    public int attack(PocketMon pocketMon) {
        return pocketMon.getPower();
    }

    @Override
    public void legendAppear() {
        System.out.println("!!!!!레전드 포켓몬 등장!!!!!");
    }
}
```
전설 포켓몬은 다음과 같이 BattleStrategyForLegend interface를 추가로 상속하여 필요한 부분에서만 메서드를 구현하도록 한다.

```java
public class Battle {
    public int attack(PocketMon pocketMon) {
        int damage = 0;

        damage = getDamage(pocketMon);

        System.out.println("%s(이/가) %d의 데미지를 주었습니다.".formatted(pocketMon.getName(), damage));
        return damage;
    }

    private static int getDamage(PocketMon pocketMon) {
        BattleStrategy battleStrategy = null;
        if ("fire".equals(pocketMon.getType())) {
            battleStrategy = new FireBattleStrategy();
        } else if ("electric".equals(pocketMon.getType())) {
            battleStrategy = new ElectronicBattleStrategy();
        } else if ("legend".equals(pocketMon.getType())) {
            LegendBattleStrategy legendBattleStrategy = new LegendBattleStrategy();
            legendBattleStrategy.legendAppear();
            battleStrategy = legendBattleStrategy;
        }

        if (battleStrategy == null) {
            throw new IllegalArgumentException("Unknown type: " + pocketMon.getType());
        }

        return battleStrategy.attack(pocketMon);
    }
}
```

마지막으로 Battle class에서는 legend인 경우 LegendBattleStrategy를 구현하여 효과를 넣어주고 이후 로직을 동일하게 가져간다.

interface를 기능에 맞게 분리함으로써 우리는 ISP를 지키게 할수 있었다.

## 🟠 DIP(Dependency Inversion Principle)

의존성 역전 원칙으로 상위 수준의 모듈은 하위 수준의 모듈에 의존해서는 안된다. 둘 모두 추상화에 의존해야 한다. 라는 의미이다.

Spring을 공부했을때 나오는 DI/IoC의 원칙과는 비슷하지만 다른 개념으로 DIP만 이해해보자.

### 🟢 수정 후 코드

우리 서비스가 너무 잘되어 유료 회원들이 생기게 되었다. 유료 회원의 경우 console에 출력되는 부분을 좀더 이쁘게 신경써주려고 한다.

```java
public interface ConsolePrint {
    void attackPrint(String pocketMonName, int damage);
}

public class NormalConsolePrint implements ConsolePrint{
    @Override
    public void attackPrint(String pocketMonName, int damage) {
        System.out.println("%s(이/가) %d의 데미지를 주었습니다.".formatted(pocketMonName, damage));
    }
}
```

ConsolePrint interface를 추가하여 하위 수준의 모듈을 추가하였다. 상위 수준의 모듈에서는

```java
public class Battle {
    private ConsolePrint consolePrint;

    public Battle(ConsolePrint consolePrint) {
        this.consolePrint = consolePrint;
    }

    public int attack(PocketMon pocketMon) {
        int damage = 0;

        damage = getDamage(pocketMon);

        consolePrint.attackPrint(pocketMon.getName(), damage);
        return damage;
    }

    ...
}
```

다음과 같이 ConsolePrint를 생성자에서 파라미터로 받아 입력되는 ConsolePrint에 따라 처리되도록 소스가 변경하였다.

```java
public class Main {
    public static void main(String[] args) {
        Battle battle = new Battle(new NormalConsolePrint());

        PocketMon 피카츄 = new PocketMon("피카츄", "electric", 10, 100);
        int 피카츄_공격 = battle.attack(피카츄);

        PocketMon 파이리 = new PocketMon("파이리", "fire", 10, 100);
        int 파이리_공격 = battle.attack(파이리);

        PocketMon 뮤츠 = new PocketMon("뮤츠", "legend", 20, 120);
        int 뮤츠_공격 = battle.attack(뮤츠);
    }
}
```

main에서도 Battle class를 생성할때 ConsolePrint를 추가로 전달하도록 작성하였다.

### 🟢 유료 회원 추가

```java
public class MemberShipConsolePrint implements ConsolePrint{
    @Override
    public void attackPrint(String pocketMonName, int damage) {
        System.out.println("🔴 %s(이/가) %d의 데미지를 주었습니다. 🔴".formatted(pocketMonName, damage));
    }
}

public class Main {
    public static void main(String[] args) {
        Battle battle = new Battle(new MemberShipConsolePrint());

        PocketMon 피카츄 = new PocketMon("피카츄", "electric", 10, 100);
        int 피카츄_공격 = battle.attack(피카츄);

        PocketMon 파이리 = new PocketMon("파이리", "fire", 10, 100);
        int 파이리_공격 = battle.attack(파이리);

        PocketMon 뮤츠 = new PocketMon("뮤츠", "legend", 20, 120);
        int 뮤츠_공격 = battle.attack(뮤츠);
    }
}
```
이제 DIP를 적용하여 Main과 Battle은 모두 추상에 의존하고 있고 어떤 ConsolePrint를 구현하여 입력하느냐에 따라 console에 출력을 다르게 할수 있게 되었다.

![](https://velog.velcdn.com/images/ililil9482/post/1ce07b09-c9ef-464e-b961-dcd39e2f4981/image.png)

이제 유료회원은 🔴 표시를 추가로 볼수 있게 되었다.

여기까지 내 머릿속의 SOLID 개념을 코드로 정리해보았다. 다른 분들도 이 글이 조금이나마 SOLID를 이해하는데 도움이 되었음 한다.