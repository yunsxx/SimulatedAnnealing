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
        System.out.println(candi);
        System.out.println(sp.fitValue(candi));
        System.out.println(sl.y);
    }
}
