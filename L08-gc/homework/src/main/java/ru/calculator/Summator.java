package ru.calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Summator {
    private int sum = 0;
    private int prevValue = 0;
    private int prevPrevValue = 0;
    private int sumLastThreeValues = 0;
    private int someValue = 0;
    // !!! эта коллекция должна остаться. Заменять ее на счетчик нельзя.
    private final List<Data> listValues = new ArrayList<>();
    private final Random random = new Random();

    // !!! сигнатуру метода менять нельзя
    public void calc(Data data) {
        listValues.add(data);
        if (listValues.size() % 100_000 == 0) {
            listValues.clear();
        }
        sum += data.value() + random.nextInt();

        sumLastThreeValues = data.value() + prevValue + prevPrevValue;

        prevPrevValue = prevValue;
        prevValue = data.value();

        int sumLastThreeSquare = sumLastThreeValues * sumLastThreeValues;
        int dataValuePlusOne = data.value() + 1;
        int listSize = listValues.size();

        someValue += sumLastThreeSquare / dataValuePlusOne - sum;
        someValue = Math.abs(someValue) + listSize;

        someValue += sumLastThreeSquare / dataValuePlusOne - sum;
        someValue = Math.abs(someValue) + listSize;

        someValue += sumLastThreeSquare / dataValuePlusOne - sum;
        someValue = Math.abs(someValue) + listSize;
    }

    public int getSum() {
        return sum;
    }

    public int getPrevValue() {
        return prevValue;
    }

    public int getPrevPrevValue() {
        return prevPrevValue;
    }

    public int getSumLastThreeValues() {
        return sumLastThreeValues;
    }

    public int getSomeValue() {
        return someValue;
    }
}
