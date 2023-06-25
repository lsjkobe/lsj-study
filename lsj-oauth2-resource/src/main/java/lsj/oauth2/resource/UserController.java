package lsj.oauth2.resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
public class UserController {

    @RequestMapping(path = "/user/info", method = {RequestMethod.GET,RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getUser(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "xxx");
        return map;
    }

    private static final String NUM_CHAR_SPECIAL_REGULAR = "(\\w|\\.|~|！|,|!|`|@|\\·|#|\\$|%|\\^|……|&|\\*|\\(|\\)|-|\\+|=|<|《|>|》|。|\\?|/|\\\\|\\[|]|\\{|}|【|】|\\||、|｜|:|;|：|；|'|\"|，|？|\\s)*";


    public static void main(String[] args) {
        Pattern numberPattern = Pattern.compile(NUM_CHAR_SPECIAL_REGULAR);
        boolean res = numberPattern.matcher("nih").matches();
        System.out.println("" + res);
    }
}