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

        for(int i =0; i < range; i++) {
            int kt = (int)t; //kt 는 t에 따른 for문 실행 횟수라고 함
            for(int j = 0; j < kt; j++) {
                double next = random.nextDouble()*(maximum-minimum)+minimum; //정해진 범위 내에서 랜덤으로 이웃값을 구함
                double f1 = sp.fitValue(next); //f1 = f(next)값

                if(sp.neighbor(f,f1) == 0) { //두 함수값을 비교해서 더 좋은 쪽의 값을 선택
                    init = next;
                    f = f1; //더 좋은 값으로 갱신
                    y.add(f);
                }
                else {
                    double d = Math.sqrt(Math.abs(f1-f)); //double q = random.nextDouble(); double d = f1-f;
                    double p = Math.exp(-d/t); // p = e^(-d/t)

                    if(random.nextDouble() < 0.0001) { //q < p
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
