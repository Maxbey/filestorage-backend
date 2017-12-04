package filestorage.validation;

import java.util.ArrayList;
import java.util.HashMap;

public class ValidationErrorResponse {
    protected ArrayList<HashMap<String, String>> errors;

    public ValidationErrorResponse(){
        errors = new ArrayList<HashMap<String, String>>();
    }

    public void addError(String field, String errorMessage){
        HashMap<String, String> error = new HashMap<String, String>();

        error.put("field", field);
        error.put("defaultMessage", errorMessage);

        errors.add(error);
    }

    public HashMap getResponse(){
        HashMap<String, ArrayList<HashMap<String, String>>> body = new HashMap<>();
        body.put("errors", errors);

        return body;
    }
}
