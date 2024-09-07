# 🔴 객체 지향

## 🟠 상속 보다는 조합을 사용하자

상속은 시멘트처럼 굳어지는 구조다. 수정이 어렵다.

상속은 부모와 자식의 결합도가 높다. 조합과 인터페이스를 활용해서 유연한 구조로 짜자

### 🟢 상속으로 해결

포켓몬이 타입에 따라 공격하는 부분을 리팩토링 해보려고 한다.

먼저 상속을 받은 경우를 해보자.

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

    private int getDamage(PocketMon pocketMon) {
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

기존 소스에서

```java
public class Battle {
    ...

    protected int getDamage(PocketMon pocketMon) {
        ...
    }
}

```

private으로 닫혀있던 getDamage 부분을 protected로 상속에서 열어준다.

```java
public class ElectricBattle extends Battle {


    public ElectricBattle(ConsolePrint consolePrint) {
        super(consolePrint);
    }

    @Override
    protected int getDamage(PocketMon pocketMon) {
        BattleStrategy battleStrategy= new ElectronicBattleStrategy();
        System.out.println("ElectricBattle 사용");
        return battleStrategy.attack(pocketMon);
    }
}
```
그리고 Battle을 상속받은 ElectricBattle, FireBattle, LegendBattle을 만들어서

```java
public class Main {
    public static void main(String[] args) {
        
        ElectricBattle electricBattle = new ElectricBattle(new MemberShipConsolePrint());
        FireBattle fireBattle = new FireBattle(new MemberShipConsolePrint());
        LegendBattle legendBattle = new LegendBattle(new MemberShipConsolePrint());

        PocketMon 피카츄 = new PocketMon("피카츄", "electric", 10, 100);
        int 피카츄_공격 = electricBattle.attack(피카츄);

        PocketMon 파이리 = new PocketMon("파이리", "fire", 10, 100);
        int 파이리_공격 = fireBattle.attack(파이리);

        PocketMon 뮤츠 = new PocketMon("뮤츠", "legend", 20, 120);
        int 뮤츠_공격 = legendBattle.attack(뮤츠);
    }
}
```

다음과 같이 사용해주면 

```java
public class Battle {
    ...

    protected int getDamage(PocketMon pocketMon) {
        BattleStrategy battleStrategy = new ElectronicBattleStrategy();
        return battleStrategy.attack(pocketMon);
    }
}
```
Battle에서 지저분하게 짜여져 있던 해당 부분은 default 값으로 전기타입으로 구현해둘 수 있게 된다. 

해당 리팩토링을 통해 지저분한 코드를 크게 개선했지만 상속의 단점은 코드가 유연하지 못하다는 것이다. 그 예시로 전기 포켓몬의 공격을 위해 ElectricBattle을 생성하여 넣어줘야 하고 그 외 불, 레전드 포켓몬 마다 새롭게 구현해야하는 문제가 발생했다.

이를 조합을 통해 더 유연한 코드로 만들어보자.

### 🟢 조합으로 해결

```java
public interface Battle2 {
    int getDamage(PocketMon pocketMon);
}
```
Battle2 라는 interface를 하나 둔다.

```java
public class ElectricBattle2 implements Battle2 {
    @Override
    public int getDamage(PocketMon pocketMon) {
        BattleStrategy battleStrategy= new ElectronicBattleStrategy();
        System.out.println("ElectricBattle2 사용");
        return battleStrategy.attack(pocketMon);
    }
}
```

```java
public class FireBattle2 implements Battle2 {
    @Override
    public int getDamage(PocketMon pocketMon) {
        System.out.println("FireBattle2 사용");
        return (int) ((pocketMon.getPower() - 11) * 1.2);
    }
}
```

```java
public class LegendBattle2 implements Battle2{
    @Override
    public int getDamage(PocketMon pocketMon) {
        BattleStrategy battleStrategy= new LegendBattleStrategy();
        System.out.println("LegendBattle2 사용");
        return battleStrategy.attack(pocketMon);
    }
}
```

다음과 같이 Battle2를 상속하여 각 배틀들을 구현해두고


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

    protected int getDamage(PocketMon pocketMon) {
        BattleStrategy battleStrategy = new ElectronicBattleStrategy();
        return battleStrategy.attack(pocketMon);
    }
}

```
기존의 Battle을

```java
public class Battle {
    private ConsolePrint consolePrint;
    private PocketMon pocketMon;
    private Battle2 battleType;

    public Battle(ConsolePrint consolePrint, PocketMon pocketMon, Battle2 battleType) {
        this.consolePrint = consolePrint;
        this.pocketMon = pocketMon;
        this.battleType = battleType;
    }

    public int attack() {
        int damage = 0;

        damage = battleType.getDamage(pocketMon);

        consolePrint.attackPrint(pocketMon.getName(), damage);
        return damage;
    }
}
```

다음과 같이 수정해준다. 이제 Battle은 Battle2의 값에 따라서 계산 로직이 해당 구현체에 따라 교체되기 때문에 더이상 내부에서 damage 계산하는 로직은 필요 없다.

```java
public class Main {
    public static void main(String[] args) {
        final ConsolePrint consolePrint = new MemberShipConsolePrint();

        PocketMon 피카츄 = new PocketMon("피카츄", "electric", 10, 100);
        int 피카츄_공격 = new Battle(consolePrint, 피카츄, new ElectricBattle2()).attack();

        PocketMon 파이리 = new PocketMon("파이리", "fire", 10, 100);
        int 파이리_공격 = new Battle(consolePrint, 파이리, new FireBattle2()).attack();

        PocketMon 뮤츠 = new PocketMon("뮤츠", "legend", 20, 120);
        int 뮤츠_공격 = new Battle(consolePrint, 뮤츠, new LegendBattle2()).attack();
    }
}
```

main에서도 다음과 같이 Battle을 구현할때 어땐 배틀 타입인지를 주입해주는 방식으로 변경되어 더욱 확장성 있게 변경되었다.

그리고 기존에 있던 Battle을 상속하여 구현해두었던 Battle들은 모두 삭제해버려도 좋다.

---

상속으로 문제를 풀었을땐 소스가 해당 코드에서 구현되어 있는 부분은 절대로 변경되어선 안되고 변경된다면 자식들까지 모두 영향이 가게 된다.

하지만 조합으로 문제를 풀게 된다면 interface로 구현된 조합 부분에 어떤 구현체를 끼워 넣느냐에 따라 동작 자체를 새롭게 정의할 수도 있어진다. 좀 더 유연하고 확장성 있는 코드가 된것이다.