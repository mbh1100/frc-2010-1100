
package team1100.season2010.robot;

public class AverageController
{
    double[] value_array;
    int size_value_array;
    int value_array_index;
    double avg_value;

    public AverageController(int size)
    {
        size_value_array = size;
        value_array = new double[size_value_array];
        value_array_index = 0;
        avg_value = 0;
    }

    public int getSize()
    {
        return size_value_array;
    }

    public AverageController()
    {
        this(5);
    }

    public double getAverageValue()
    {
        return avg_value;
    }

    public void addNewValue(double newValue)
    {
        avg_value =  avg_value*size_value_array;
        avg_value -= value_array[value_array_index%size_value_array];
        avg_value += newValue;
        avg_value =  avg_value/size_value_array;
        value_array[value_array_index%size_value_array] =  newValue;
        value_array_index++;
    }

}
