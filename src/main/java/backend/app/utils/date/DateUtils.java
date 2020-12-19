package backend.app.utils.date;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {

    public static List<Date> getFirstDayAndLastDayByMonthAndYear(Integer year, Integer month) {
        List<Date> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        // Obtengo el primer dia segun el año y el mes de los parametros
        calendar.set(year, month-1, 1);
        list.add(calendar.getTime());
        // Obtengo el ultimo dia del mes segun parametros
        calendar.set(year, month-1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        list.add(calendar.getTime());
        return list;
    }
}
