`출처` [인프런 Readable Code](https://www.inflearn.com/course/lecture?courseSlug=readable-code-%EC%9D%BD%EA%B8%B0%EC%A2%8B%EC%9D%80%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1%EC%82%AC%EA%B3%A0%EB%B2%95&unitId=207880&tab=curriculum)

# 🔴 추상

## 🟠 추상

### 🟢 추상이란?

추상의 사전적 의미는 사물을 정확하기 이해하기 위해 사물의 특정한 측면만을 가려내어 포착하고 다른 측면들은 버린다는 것이다.

### 🟢 추상과 구체

구체는 구체적으로 구현된 물체이고 추상은 반대로 구체적인 부분에서 특정한 측면만을 가지고 있는 것이다.  
예를 들어

```
나는 어제 다른 업체의 API를 호출하여 우리 DB에 데이터를 밀어넣고 해당 데이터들을 다시 우리 데이터에 맞게 API에서 반환할 수 있는 작업을 진행해보았어.
```

라는 말은 구체적으로 내가 어떤 일을 했는지 설명하고 있다. 이 말을 추상적으로 표현한다면

```
나는 어제 ERP 연동을 했어.
```

라고 표현할 수 있다.  
추상이란 추상 메서드, 추상 클래스와 같이 어려운 단어로 보기보다. 우리 인간이 어떠한 구체적인 내용을 함축하고 필요없는 부분을 제거하여 표현하는 것이라고 볼 수 있다.

### 🟢 컴퓨터와 추상

컴퓨터 과학에서 추상은 엄청난 힘을 발휘하고 있다. 그 이유는 컴퓨터는 0=false, 1=true라는 간단한 정의만을 연산한다. 그런데 어떻게 컴퓨터로 게임도 하고, 인터넷 방송도 하며 지금처럼 글도 쓸수 있는걸까? 그것은 바로 추상의 힘이다.

101이란 숫자를 ㄱ으로 정의하고 1001이라는 숫자는 ㄴ으로 정의한다. 이렇게 추상을 통해 우리는 1,0만 사용할 수 있는 컴퓨터를 통해 현재 하는 모든 것들을 정의하여 사용할 수 있는 것이다.

## 🟠 이름짓기

### 🟢 이름을 짓는 행위도 추상에 기반한다.

개발자로써 가장 고통스러운 행위는 메서드명과 변수명을 지을 때이다. 그 이유는 어떤 방법으로 이름을 지어야 좋을지 모르겠기 때문이다. 이럴땐 이름 짓기 규칙을 기억하자.

```
1. 단수와 복수 구분
2. 줄여서 사용하지 않기 (관용어는 사용 o)
3. 은어/방어/유행어 사용하지 않기
4. 좋은 코드에서 배우기
```

1.  단수와 복수 구분  
    끝에 (e)s를 붙여 복수인지 단수인지를 구분하는 것이 좋다. List라면 list를 붙이는 것도 좋다.
2.  줄여서 사용하지 않기  
    ChoiJunHo라는 단어를 cjh로 줄인다면 내가 아닌 이상 이 단어가 choi jun ho를 의미하는지 아무도 모를 것이다. 하지만 column을 col이라던지 latitude를 lat, longtitude를 lon으로 줄여 관용어처럼 사용하는 것 정도는 괜찮다.
3.  은어/방언/유행어 사용하지 않기  
    팀내에서만 사용되는 언어, 나만 아는 언어, 현재 유행하는 언어들로 짓지 않는다. 그 이유는 개발자는 계속 바뀔텐데 나중에 참여한 인원이 현재 이름에 대해 이해할 수 없을 수 있기 때문이다. 해당 내용을 방지하기 위한 것으로는 도메인 용어 사전을 팀내에 만들어서 사용하는 것도 좋은 해결 방법이다.
4.  좋은 코드에서 배우기  
    잘 짜여진 코드들에서는 변수명을 알맞게 잘 갖다 쓴 경우가 보인다. 해당 변수명을 학습해서 나도 똑같이 갖다 쓰는 것을 추천한다.

## 🟠 메서드

### 🟢 한 메서드의 주제는 반드시 하나다.

메서드를 생성할때 해당 개념을 꼭 기억하자.

```
나는 엄마에게 용돈을 받았다. 그리고 친구들과 과자를 사먹었고 남은 돈으로 통장에 저금을 했다.
```

위 글에서 우리는 행동을 몇개로 정의할 수 있을까?

1.  용돈을 받았다.
2.  과자를 사먹었다.
3.  저금을 했다.  
    이렇게 3개의 주제를 가져올 수 있을 것이다.  
    이 행위를 메서드로 생각하면

    ```
    Money 용돈을_받다() {};
    Buy 과자를_사먹다(Money) {};
    Saving 저금을_하다(Buy) {};
    ```

    이렇게 하나의 메서드는 한개의 주제만을 가지도록 한다.

그럼 에 메서드들로 다시 내용을 구성한다면

```
void 오늘_하루_일과() {
    Money money = 용돈을_받다();
    Buy buy = 과자를_사먹다(money);
    Saving saving = 저금을_하다(buy);
}
```

로 오늘 하루 일과라는 메서드로도 만들어낼 수 있다.  
메서드를 쪼갤때는 항상 하나의 메서드의 하나의 주제를 기억하자.

### 🟢 메서드명

메서드명은 추상화된 구체를 유추하기 쉽도록 적절한 의미가 담긴 이름이면 좋다. 또한 메서드명과 파라미터를 연결지어 더 풍부한 의미를 전달할 수 있다.

```
public class Scribbling {
    public static void main(String[] args) {

        List<PocketMon> myPocketMon = new ArrayList<>();

        List<PocketMon> pocketMonList = new ArrayList<>();
        pocketMonList.add(new PocketMon("피카츄"));
        pocketMonList.add(new PocketMon("파이리"));
        pocketMonList.add(new PocketMon("꼬부기"));
        pocketMonList.add(new PocketMon("이상해씨"));

        double random = Math.random() * pocketMonList.size();
        PocketMon pickupPocketMon = pocketMonList.get((int) random);

        // 포켓몬은 5마리만 가져갈 수 있다.
        if (myPocketMon.size() < 6) {
            myPocketMon.add(pickupPocketMon);
        }

    }

    public static class PocketMon {
        private String name;

        public PocketMon(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
```

위와 같은 코드가 있다. 포켓몬 뽑기를 하는 코드인데 이 부분을 메서드로 잘개 쪼개보자.

```
public class Scribbling {
    public static void main(String[] args) {
        List<PocketMon> myPocketMonList = new ArrayList<>();
        List<PocketMon> pocketMonList = createPickupPocketMonList();
        PocketMon pickupPocketMon = randmonPickupFrom(pocketMonList);
        String resultText = pocketMonAddResultText(myPocketMonList, pickupPocketMon);
        System.out.println(resultText);
    }

    private static String pocketMonAddResultText(List<PocketMon> myPocketMonList, PocketMon pickupPocketMon) {
        return isAddMyPocketMonList(myPocketMonList, pickupPocketMon) ?
                String.format("%s(을)를 추가했습니다.", pickupPocketMon.getName()) : "이미 포켓몬 리스트가 가득 찼습니다.";
    }

    private static Boolean isAddMyPocketMonList(List<PocketMon> myPocketMon, PocketMon pickupPocketMon) {
        if (isSparePocketMonList(myPocketMon)) {
            myPocketMon.add(pickupPocketMon);
            return true;
        }
        return false;
    }

    private static boolean isSparePocketMonList(List<PocketMon> myPocketMon) {
        // 포켓몬은 5마리만 가져갈 수 있다.
        return myPocketMon.size() < 6;
    }

    private static PocketMon randmonPickupFrom(List<PocketMon> pocketMonList) {
        double random = Math.random() * pocketMonList.size();
        PocketMon pickupPocketMon = pocketMonList.get((int) random);
        return pickupPocketMon;
    }

    private static List<PocketMon> createPickupPocketMonList() {
        List<PocketMon> pocketMonList = new ArrayList<>();
        pocketMonList.add(new PocketMon("피카츄"));
        pocketMonList.add(new PocketMon("파이리"));
        pocketMonList.add(new PocketMon("꼬부기"));
        pocketMonList.add(new PocketMon("이상해씨"));
        return pocketMonList;
    }

    public static class PocketMon {
        private String name;

        public PocketMon(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
```

다음과 같이 메서드별로 쪼개어 main 함수의 내용을 더 보기 좋게 만들었다.  
여기서 보고 갈 메서드만 잠깐 보고 가자면

`randmonPickupFrom` 메서드는 메서드명에 전치사를 줌으로써 pocketMonList로 부터 랜덤으로 픽업한다. 라는 의미를 좀더 정확하고 풍부하게 전달할 수 있다.  
`pocketMonAddResultText` 메서드는 해당 메서드 내에서 실행되는 의미를 전부 포함하고 있다. 메서드 내에서는 포켓몬이 add되었는지 여부를 체크하며 해당 내용을 사용자에게 메세지로 반환해준다.

### 🟢 파라미터

위 코드에서 파라미터의 중요성도 알수 있는데 `randmonPickupFrom()` 메서드의 파라미터는 `List<PocketMon> pocketMonList`로 명확한 리스트를 전달하도록 했다. 만약 어차피 이름만 랜덤으로 뽑을거라며 `String[] list` 와 같은 리스트를 전달한다면 사용하는 개발자 입장에서 어떤 String 배열을 넣어야할지 모를 것이다.

### 🟢 반환타입

메서드에서 반환 타입은 void 대신 반환할 값이 충분히 있다면 반환하는게 test를 용이하게 만들어 더 좋을 수 있다.

## 🟠 추상화 레벨

추상화 레벨이란 코드가 얼마나 추상화가 되었는지에 대한 값으로 볼 수 있다.  
예를 들어

```
int level = 1;
if (level == 1) {
    System.out.Println("테스트 입니다.");
}
```

추상 레벨이 높을수록 추상화가 많이 되었다라고 볼때 다음 코드의 추상레벨은 1이다. 컴퓨터 코드 그대로 표현되고 있기 때문이다.

```
int level = 1;
if (isOne(level)) {
    System.out.Println("테스트 입니다.");
}

private Boolean isOne(int level) {
    return level == 1;
}
```

메서드를 통해 조건 부분을 추상화한 것은 추상화 레벨을 올려 10으로 볼수 있다.

그럼 왜 굳이 이런 추상화 레벨을 체크해야될까? 위에 코드만 봐도 첫번째 코드가 더 간결하고 오히려 더 보기 좋은 코드이지 않을까?

### 🟢 추상화 레벨을 맞춰야 읽기 좋다.

```
public class Scribbling {
    public static void main(String[] args) {
        List<PocketMon> myPocketMonList = new ArrayList<>();
        List<PocketMon> pocketMonList = createPickupPocketMonList();
        if (pocketMonList.size() < 0) {
            throw new RuntimeException("포켓몬 리스트가 피어 있습니다.");
        }
        PocketMon pickupPocketMon = randmonPickupFrom(pocketMonList);
        String resultText = pocketMonAddResultText(myPocketMonList, pickupPocketMon);
        System.out.println(resultText);
    }
    ...
}
```

예를 들어 위에 코드를 보자. 코드의 추상 레벨이 중간에 if 조건에서만 갑자기 낮아진다. 이렇게 되면 코드를 읽는 개발자는 추상 레벨이 높은 코드를 보다가 갑자기 추상 레벨이 낮은 코드를 보게 되는데 이렇게 되면 코드를 이해하는데 시간이 걸리게 된다. 물론 지금 코드는 짧게 짜여져 있기 때문에 그렇지만 복잡한 로직일 수록 중간에 이런 코드들이 섞여있다면 개발자는 코드를 이해하기가 더 어려워질 수 있다.

```
public class Scribbling {
    public static void main(String[] args) {
        List<PocketMon> myPocketMonList = new ArrayList<>();
        List<PocketMon> pocketMonList = createPickupPocketMonList();
        if (isListEmptyFrom(pocketMonList)) {
            throw new RuntimeException("포켓몬 리스트가 피어 있습니다.");
        }
        PocketMon pickupPocketMon = randmonPickupFrom(pocketMonList);
        String resultText = pocketMonAddResultText(myPocketMonList, pickupPocketMon);
        System.out.println(resultText);
    }
    ...
}
```

다음과 같이 코드의 추상화 레벨을 맞춰준다면 훨씬 더 읽기 좋은 코드가 될수 있다.

## 🟠 매직넘버, 매직스트링

코드를 작성하며 의미가 있는 숫자, 의미가 있는 문자열이 존재한다. 그런 코드들의 의미를 부여해주는 것이 코드를 읽는데 큰 도움이 된다.

```
    public static void main(String[] args) {
        List<PocketMon> myPocketMonList = new ArrayList<>();
        List<PocketMon> pocketMonList = createPickupPocketMonList();
        if (pocketMonList.size() < 0) {
            throw new RuntimeException("포켓몬 리스트가 피어 있습니다.");
        }
        PocketMon pickupPocketMon = randmonPickupFrom(pocketMonList);
        String resultText = pocketMonAddResultText(myPocketMonList, pickupPocketMon);
        System.out.println(resultText);
    }
```

예를 들어 위에 코드에서도 조건에 0이라는 숫자가 있다. 이 숫자에 의미를 부여하면

```
    public static final int MIN_POKEMONS = 0;

    public static void main(String[] args) {
        List<PocketMon> myPocketMonList = new ArrayList<>();
        List<PocketMon> pocketMonList = createPickupPocketMonList();
        if (pocketMonList.size() < MIN_POKEMONS) {
            throw new RuntimeException("포켓몬 리스트가 피어 있습니다.");
        }
        PocketMon pickupPocketMon = randmonPickupFrom(pocketMonList);
        String resultText = pocketMonAddResultText(myPocketMonList, pickupPocketMon);
        System.out.println(resultText);
    }
```

다음과 같이 의미를 파악하기 훨씬 쉬워진다.  
현재 코드는 짧아서 굳이 필요하려나 싶어질 정도지만 더 복잡한 코드로 가게 된다면 해당 부분이 큰 도움이 되는 코드일 것이다.

# 👏 정리

이번에 공부하게된 내용을 정리하자면

1.  추상과 구체에 대한 정확한 구분
2.  이름짓기의 중요성과 단축어를 사용하지 말아야할 이유
3.  메서드를 선언하는 방법
4.  추상화 레벨을 맞춰서 코드를 작성해야되는 이유
5.  매직 넘버, 매직 스트링은 상수로 빼서 사용할때 장점  
    내용에 대해 학습을 진행할 수 있었다!