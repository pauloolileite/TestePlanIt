package Utils;

import javax.swing.*;
import java.text.SimpleDateFormat;

public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public Object stringToValue(String text) throws java.text.ParseException {
        return dateFormatter.parse(text);
    }

    @Override
    public String valueToString(Object value) throws java.text.ParseException {
        if (value != null) {
            return dateFormatter.format(((java.util.Calendar) value).getTime());
        }
        return "";
    }
}