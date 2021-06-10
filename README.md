(시작하기 전 솔직하게 말씀드리면 모의담금질 기법 전혀 감이 잡히지 않아서 코드를 짜지 못했습니다... 교수님 코드와 ppt를 보고 간신히 이해라도 한 수준이랄까요...
그래서 코드를 직접 구현한 게 아니라 그냥 교수님 코드를 보면서 어떻게 모의담금질을 구현할 수 있는 지에 대한 이해와 코드 분석을 해본 거라고 하는 게 맞을 거 같습니다...
이것저것 참고는 해봤지만 코드는 못 짜겠어서... 하하 그래도 아무것도 안하는 것보다 이거라도 해야 무언가 남을 거 같아서 readme file이라도 끄적여보았습니다)

### Simulated Annealing(모의담금질 기법)

> 온도가 높을 때는 움직일 수 있는 방향이 다양하고, 식으면서 점점 온도가 내려갈 때 움질일 수 있는 방향이 줄어들게 되는 현상을 모티브로 하는 알고리즘이다. 



> 후보해가 하나 존재하고 이웃해들 중에서 하나를 선택해 접근해보는 방식. 
>
> 유전자알고리즘과 다른 점이 있다면 유전자 알고리즘은 여러 개의 후보해를 가진다면 모의담금질 기법에서는 하나의 후보를 가진다.
>
> 이웃해가 더 `좋은 해`인가 `나쁜 해`인가를 가지고 후보해를 선택하게 되는데 이때 확률을 가지고 경우를 나누기 때문에 
>
> 현재의 해보다 좋은 해는 아니지만 조건에 만족된다면 `나쁜 해임에도 불구하고` 접근하게 될 수 있다. 



#### Problem

> 3~4차 function의 **전역 최적점**을 찾을 수 있는 모의담금질 기법을 구현.
>
> curve fitting을 위한 선형 or 비선형 모델을 정하고 가장 적합한 파라미터 값을 구해라. (모의담금질 기법으로)



#### <code - main()>

```java
import Problem.SimpleProb;

public class Main {
    public static void main(String [] args) {
        double t = 180; //초기 온도
        double coolingRatio = 0.89; //alpha 값 ( t = at 에서 쓰임)
        double minimum = 0; //최소 x
        double maximum = 15; //최대 x
        double init = 0; //초기 x 값 -> 따라서 출력 list 에서 첫 값이 함수의 상수값이 됨

        Simulated sl = new Simulated(10);
        SimpleProb sp = new SimpleProb();

        double candi = sl.perform(sp,t,coolingRatio,minimum, maximum, init);
        System.out.println("전역 최적값: "+candi);
        System.out.println("전역 최적값의 함수값: "+ sp.fitValue(candi));
        System.out.println(sl.y);
    }
}
```

> 모의담금질 기법은 초기의 온도에서 점점 온도가 내려가면서 실행된다. 따라서 초기온도와 냉각률(coolingRatio)이 쓰이게 된다. 
>
> 임의로 정해줄 초기후보값을 init, 후보해를 선정할 때 범위로 사용될 minimum과 maximun이 변수로 만들어져 초기화되어있다. 
>
> 각 클래스형의 객체를 선언해주는 simpleProb는 다른 package에 있는 클래스이므로 import를 작성해준다. 



#### <code - Problem>

```java
package Problem;

public interface Problem {
    double fitValue(double x);
    int neighbor(double n1, double n2);
}
```

> 클래스 simpleprob에서 구현할 함수들을 미리 작성해둔다. 



#### <code - SimpleProb>

```java
package Problem;

public class SimpleProb implements Problem{
    @Override
    public double fitValue(double x) {
        //f`(x) = 6x^2-60x+96
        //도함수는 2와 8에서 0이 된다.
        //f(2) = 95, f(8) = -121
        return 2*x*x*x-30*x*x+96*x+7;
    }

    @Override
    public int neighbor(double n1, double n2) {
        if(n1 < n2)
            return 1;
        else //n1 > n2 of n1 == n2
            return 0;
    }
}
```

> fitvalue는 선정된 값에 따른 함수값을 도출하는 함수
>
> neighbor은 두 개의 해를 비교하여 비교값을 도출하는 함수이다. 

위 두 코드는 하나의 package에 들어있는 인터페이스와 클래스이다. 



#### <code - Simulated>

```java 
import Problem.SimpleProb;

import java.util.ArrayList;
import java.util.Random;

public class Simulated {
    int range;
    public ArrayList<Double> y = new ArrayList<>(); //함수값을 바로바로 저장시킬 list(array는 사용하기가 좀..)

    public Simulated(int range) //Simulated 형 객체가 만들어질 때 자동으로 실행되는 메소드
    {
        this.range = range;  //this 를 통해서 해당 객체의 range값에 parameter 로 받은 값을 대입시킴
    }

    public double perform(SimpleProb sp, double t, double coolingRatio, double minimum, double maximum, double init)
    {
        Random random = new Random(); //random 객체 생성
        double f = sp.fitValue(init); //초기값을 함수에 넣어 함수값을 얻고
        y.add(f); //그 값을 list 에다가 저장

        //랜덤으로 이웃해 선택
        //이웃해가 더 우수한 경우 s<-s`
        //아닌 경우 p와 q의 확률에 따라서 나쁜 해에서 접근할 기회를 줌
        //t 는 a (cooling ratio)에 따라 감소하게 됨 => t = a*t
        //a는 <= 0.99, 0.99에 가까울수록 천천히 감소됨
        //t,p,d의 관계를 정리한 식은  p = e^-d/t
        
        //여기서부터 ppt의 내용과도 같은 맥락의 코드임 
        for(int i =0; i < range; i++) {
            int kt = (int)t; //kt 는 t에 따른 for문 실행 횟수라고 함
            for(int j = 0; j < kt; j++) {
                double next = random.nextDouble()*(maximum-minimum)+minimum; 
                //정해진 범위 내에서 랜덤으로 이웃값을 구함
                double f1 = sp.fitValue(next); //f1 = f(next)값

                if(sp.neighbor(f,f1) == 0) { //두 함수값을 비교해서 더 좋은 쪽의 값을 선택
                    init = next;
                    f = f1; //더 좋은 값으로 갱신
                    y.add(f);
                }
                else {
                    double d = Math.sqrt(Math.abs(f1-f)); 
                    //-> (ppt대로하면) double q = random.nextDouble(); double d = f1-f;
                    double p = Math.exp(-d/t); // p = e^(-d/t)

                    if(random.nextDouble() < 0.0001) { //-> q < p
                        init = next;
                        f = f1; //계속해서 x의 값과 그에 따른 함수값을 갱신
                        y.add(f); //함수값을 list에 저장
                    }
                }
            }
            t = coolingRatio*t; // t = at t가 a에 따라 점점 감소
        }
        return init; //제일 최적인 x좌표의 값을 return
    }
}
```

> 모의담금질을 실행하는 함수
>
> 모의담금질 기법은 선택할 해가 현재의 해보다 더 좋은 해가 아니더라도 q와 p를 비교해서 조건을 만족한다면
>
> 선택하려고 한 해에 접근할 기회를 준다는 특징이 있다. 



> 여기서 alpha값을 나타내는 a는 0.8에서 0.99까지의 속하는 하나의 수이고 (0.8 <=  a  <= 0.99)
>
> 보통 a가 0.99에 가까이갈수록 천천히 감소된다. (t에 곱해지는 수이다보니 a가 높을 수록 온도가 덜 떨어지는 것이 당연)
>
> 따라서 코드에서의 coolingRatio값을 바꿈으로써 온도 t의 냉각률을 조절할 수 있다. 
>
> (냉각률을 높이고 싶다면 0.8에 가깝게, 냉각률을 낮추고 싶다면 0.99에 가깝게)





> 코드를 실행시키면 결과가 다음과 같이 출력된다. 

![image](https://user-images.githubusercontent.com/80511175/121477259-94cadc00-ca02-11eb-8c7e-5227ff77bd5b.png)



![image](https://user-images.githubusercontent.com/80511175/121477460-d5c2f080-ca02-11eb-8738-d6a7a5687737.png)

- 실행할 때마다 값은 조금씩 다르게 나오지만 실제로 구한 극소점에서의 값 -121에 거의 근접한 값이 결과로 나오는 것을 알 수 있다. 

  > f(x) = 2x^3-30x^2+96x+7 / f(8) = -121 
  >
  > 전역 최적값이 거의 8에 근접한 값임을 알 수 있고 그에 따라 함수값도 -121에 굉장히 근사하게 나오는 것을 볼 수 있다. 





#### <실행시간> 

```java
for(int i =0; i < range; i++) {
        int kt = (int)t; //kt 는 t에 따른 for문 실행 횟수라고 함
        for(int j = 0; j < kt; j++) {
            double next = random.nextDouble()*(maximum-minimum)+minimum; 
            //정해진 범위 내에서 랜덤으로 이웃값을 구함
            double f1 = sp.fitValue(next); //f1 = f(next)값

            if(sp.neighbor(f,f1) == 0) { //두 함수값을 비교해서 더 좋은 쪽의 값을 선택
                init = next;
                f = f1; //더 좋은 값으로 갱신
                y.add(f);
            }
            else {
                double d = Math.sqrt(Math.abs(f1-f)); 
                //-> (ppt대로하면) double q = random.nextDouble(); double d = f1-f;
                double p = Math.exp(-d/t); // p = e^(-d/t)

                if(random.nextDouble() < 0.0001) { //-> q < p
                    init = next;
                    f = f1; //계속해서 x의 값과 그에 따른 함수값을 갱신
                    y.add(f); //함수값을 list에 저장
            }
        }
    }
    t = coolingRatio*t; // t = at t가 a에 따라 점점 감소
}
```

> 첫번째 for문은 range(=n)만큼 실행된다. 
>
> 두번째 for문은 현재의 t와 a가 얼마냐에 따라서 달라지게 된다. 
>
> => O(n) * (kt + kat + ka^2t + ka^3t + ... + ka^(n-1)t )
>
> t와 a의 수가 작을 수록 실행시간은 줄어들게 되고 n의 값이 커질수록 실행시간은 늘어나게 된다. 
