`출처` [인프런 Readable Code](https://www.inflearn.com/course/lecture?courseSlug=readable-code-%EC%9D%BD%EA%B8%B0%EC%A2%8B%EC%9D%80%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1%EC%82%AC%EA%B3%A0%EB%B2%95&unitId=207880&tab=curriculum)

# 🔴 논리 사고 흐름

인간의 뇌는 한번에 한가지만 처리할 수 있다. 멀티 태스킹을 잘한다는 것은 여러 업무를 번갈아 가면서 처리할때 빠르게 다른 업무를 처리할 준비를 할 수 있는 능력이 좋다는 것을 의미한다.  
이 말은 즉 우리의 뇌는 업무를 처리할때 `업무 처리를 위한 준비 -> 업무 처리 -> 다른 업무 인지 -> 업무 처리를 위한 준비` 형태로 진행된다는 것이다.  
이를 코드를 작성할때도 고려해볼만 하다.

## 🟠 Early Retrun

코드를 작성할때 빠른 리턴은 코드를 더 읽기 쉽게 해준다.

```
public class Scribbling2 {
    @Test
    void earlyReturn() {
        int count = 3;

        if (count < 0) {
            System.out.println("hello 0");
        } else if (count < 2) {
            System.out.println("hello 1");
        } else {
            System.out.println("hello 2");
        }
    }
}
```

다음 코드를 실행했을때 결과는 `hello 2` 가 출력된다는 것은 다 알것이다. 하지만 해당 코드를 해석하려면 인간의 뇌는

```
count가 0보다 작다면 hello 0 이 출력되고 hello가 0을 초과하고 2보다 작으면 hello1이 출력되고 count가 0보다 크고 2보다도 크면 hello 2가 출력된다.
```

라는 긴 해석을 가지게 된다. 이를 Early Return으로 좀더 읽기 좋게 만들어보자.

```
public class Scribbling2 {
    @Test
    void earlyReturn() {
        int count = 3;

        printHello(count);
    }

    private static void printHello(int count) {
        if (count < 0) {
            System.out.println("hello 0");
            return ;
        }

        if (count < 2) {
            System.out.println("hello 1");
            return;
        }
        System.out.println("hello 2");
    }
}
```

위에 코드는 기존과 동일하다. 하지만 인간의 뇌는 이를 어떻게 해석할까?

```
count가 0보다 작으면 hello 0이 출력된다.
```

```
count가 2보다 작으면 hello 1이 출력된다.
```

```
그 외에는 hello 2가 출력된다.
```

다음과 같이 쪼개서 코드를 읽어낼 수 있게 된다. 이는 사람마다 차이는 있을 수 있겠지만 Early Return을 통해 코드를 읽어냄에 있어 좀 더 간결해지고 else if 지옥에 빠지게 하지 않을 수 있는 코드가 된다.

## 🟠 사고의 depth 줄이기

### 🟢 사용할 변수는 가깝게 선언하기

```
@Test
void depth() {
int count = 3;

// 코드 30줄...

printHello(count);
}
```

이러한 코드에서 count가 가장 위에 선언되어 있고 30줄 뒤에 count를 사용하고 있다면 인간의 뇌는 count가 몇이였는지 뇌에서 뒤져서 찾아내거나 실제 소스를 다시 읽어야 하는 상황이 발생한다. 이를 최대한 줄이기 위해 `사용할 변수는 가깝게 선언하기`를 해준다면 더 읽기 좋은 소스가 될수 있다.

### 🟢 공백으로 의미 분기하기

```
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

```
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

위에 두 소스는 동일한 소스인데 공백을 통해 의미를 분리한 소스가 훨씬 더 읽기 쉽다. 공백을 의미없이 두지 말고 의미를 분기처리하는 용도로 사용하여 코드를 더 읽기 쉽게 만들어보자.

### 🟢 중첩 반복문, 중첩 조건문 줄이기

```
    @Test
    void depth2() {
        for (int i=0; i<30; i++) {
            for (int j=0; j<10; j++) {
                if (i > 10 && j < 5) {
                    System.out.println("출력");
                }
            }
        }
    }
```

2중 for문에 조건문을 더한 코드가 있다면 다음과 같이 작성할 수 있다.

```
i는 0부터 29까지 진행하고 j는 0부터 9까지 진행한다. 그 중 i가 10보다 크고 j가 5보다 작으면 출력한다.
```

인간의 뇌로는 이렇게 해석하고 있을 것이다. 복잡하게 느껴진다. 이 코드를 줄여보자.

```
    @Test
    void depth2() {
        for (int i=0; i<30; i++) {
            printByI(i);
        }
    }

    private static void printByI(int i) {
        for (int j=0; j<10; j++) {
            if (i > 10 && j < 5) {
                System.out.println("출력");
            }
        }
    }
```

```
i는 0부터 29까지 진행한다. printByI에 의해 출력된다.
```

```
printByI는 j가 0분터 9까지 진행하며 i가 10보다 크고 5보다 작은 경우 출력된다.
```

이렇게 나눠서 더 쉽게 이해할 수 있다.

### 🟢 부정어 처리하기

```
if (! isClosed()) {
doSomething();
}
```

위 코드처럼 부정어를 사용하는 경우 사람의 뇌에서는

```
isClosed() 조건에 맞게 생각 후 해당 부분에 대해 반대로 생각해야지
```

라는 고민을 하게 된다. 하지만 이런 부정어를

```
if (isOpened()) {
}

if (isNotClosed()) {
}
```

와 같이 부정어 없이 명확하게 사용해준다면 더 읽기 좋은 코드가 된다.

## 🟠 해피 케이스와 예외 처리

### 🟢 예외 발생 가능성 낮추기

해피 케이스 대로만 프로그램이 실행되면 좋겠지만 예외가 발생하는 경우를 피할 순 없다. 그래서 프로그램을 작성하며 검증을 통해 예외를 발생시킬 가능성을 낮춰야 한다.

### 🟢 검증이 필요한 부분은 주로 외부에서 접점이 있는 부분

검증이 필요한 경우는 사용자가 프로그램에 요청을 하거나, 프로그램에서 외부 프로그램으로 통신을 하는 경우, 통신하여 데이터를 받아와 읽는 경우로 주로 외부와 접점이 있는 부분에 검증이 필요한 경우가 많다.

### 🟢 의도한 예외와 의도하지 않은 예외 구분하기

의도된 예외 예를 들어 검증을 통해 사용자에게 아이디 or 비밀번호를 잘못 입력했다는 예외를 반환해야할 경우는 의도된 예외이고 그 외 프로그램에서 개발자가 생각지 못하게 발생하는 예외가 의도하지 않은 예외다. 이 경우 의도한 예외면 사용자에게 반환하도록 처리하고 의도하지 않은 예외라면 로그로만 찍던지 개발자가 해당 부분을 인지할 수 있도록 처리한다.

```
try {
... 로직
    if (isFail()) {
     throw new Myexception("의도한 예외 발생");
    }
} catch (MyException me) {
log.info(me.getMessage());
System.out.println("의도한 예외입니다.");
} catch (Exception e) {
log.error(e);
System.out.println("의도하지 않은 예외입니다.");
}
```

### 🟢 항상 null을 고려하기

null은 항상 언제든 일어날 수 있고 null이 발생한다면 NullPointException이 발생한다는 것이다 NullPointException이 발생하지 않도록 항상 처리해주자.

그중 메서드 설계시 null 반환을 자제하는 것이 좋다. 만약 null을 꼭 반환해야 한다면 Optional을 고려해볼 수 있다.

### 🟢 Optional은 비싼 객체다

하지만 Optional은 비싼 객체이기 때문에 사용할때도 고민해보는 것이 좋다. 그리고 Optional을 메서드 파라미터로 받지 않는 것이 좋다. 그 이유는 파라미터로 넘겼을 경우

```
파라미터가 null인지 (=Optional이 null인지)
Optional 안에 데이터가 null인지
null 이 아니라면
```

이렇게 분기를 3가지나 해야되기 때문이다. 최대한 메서드의 파라미터는 온전한 객체로 넘기도록 하자.

### 🟢 Optional 사용방법

하지만 Optional은 java에서 null을 다루기 좋은 방법 중 하나다. Optional을 해소하는 방법으로는 `orElse()` `orElseGet()` `orElseThrow()` `isPresent()` `isPresentOrElse()`가 존재하는데 그중 `orElse()` 와 `orElseGet()`의 차이를 알고 사용하자.

```
    public T orElse(T other) {
        return value != null ? value : other;
    }
```

```
    public T orElseGet(Supplier<? extends T> supplier) {
        return value != null ? value : supplier.get();
    }
```

`orElse()`와 `orElseGet()`의 로직을 확인해보면 위와 같다.

```
Object obj = null;
Optional.ofNullable(obj).orElse(doSomething());
Optional.ofNullable(obj).orElseGet(doSomething());
```

obj값이 null일때 두개의 동작 차이는 어떨까? `orElse()`의 경우 조건식에서 doSomething의 결과를 반환하여 갖고 있어야 하기 때문에 로직이 바로 실행된다. 반면 `orElseGet()`의 경우 obj가 null인지 체크하고 null 이라면 그때 doSomething()을 실행하기 때문에 비용 측면에서 아낄 수 있다.