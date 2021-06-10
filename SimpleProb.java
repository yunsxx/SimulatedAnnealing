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
