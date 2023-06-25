package lsj.oauth2.resource;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TestReverse {

    private static final Integer SHORT_ADDRESS = 4;
    private static final String NUM_CHAR_SPECIAL_REGULAR = "(\\w|\\.|~|！|,|!|`|@|\\·|#|\\$|%|\\^|……|&|\\*|\\(|\\)|-|\\+|=|<|《|>|》|。|\\?|/|\\\\|\\[|]|\\{|}|【|】|\\||、|｜|:|;|：|；|'|\"|，|？|\\s)*";

    private static final String PHONE = "[0]\\d{2,3}[-]\\d{7,8}";

    private static final String MOBILE = "[1][3-9][0-9]{9}";

    private static final String PHONE_EXTENSION_NUMBER = "[0]\\d{2,3}[-]\\d{7,8}[-]\\d{1,4}";

    private static final String ENCRYPTION_STRING = "*";

    private static final Character ENCRYPTION_CHAR = new Character('*');

    private static final Integer ENCRYPTION_CHAR_BOTTOM = 5;

    public static void main(String[] args) {
        TestReverse testReverse = new TestReverse();
        UserInfo userInfo = testReverse.buildUserInfo("中国^^^^^^^^^^^^广东省~~~东莞市~~~东城街道~~~~~~测试");
        testReverse.validUserInfo(userInfo);
    }

    public UserInfo buildUserInfo(String address) {
        UserInfo userInfo = new UserInfo();
        ContactDO refunder = new ContactDO();
        refunder.setPhone("13418954434");
        refunder.setMobilePhone("13418954434");
        Address finalAddress = AddressUtils.parseAddressFromLC(address);
        userInfo.setPhone(getPhone(refunder));
        userInfo.setName("李上健");
        userInfo.setProvince(finalAddress.getProvince());
        userInfo.setCity(finalAddress.getCity());
        userInfo.setCounty(finalAddress.getDistrict());
        userInfo.setTown(finalAddress.getStreet());
        userInfo.setAddress(finalAddress.getDetailAddress());
//        context.putParam(ReverseBizConstants.RT_RECEIVER, receiver);
        return userInfo;
    }

    public boolean validUserInfo(UserInfo userInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isBlank(userInfo.getProvince())) {
            return false;
        } else {
            stringBuilder.append(userInfo.getProvince());
        }
        if (StringUtils.isBlank(userInfo.getCity())) {
            return false;
        } else {
            stringBuilder.append(userInfo.getCity());
        }
        if (StringUtils.isNotBlank(userInfo.getCounty())) {
            stringBuilder.append(userInfo.getCounty());
        }
        if (StringUtils.isNotBlank(userInfo.getTown())) {
            stringBuilder.append(userInfo.getTown());
        }
        if (StringUtils.isBlank(userInfo.getAddress())) {
            return false;
        } else {
            String detailAddress = userInfo.getAddress().trim();
            if (this.invalidAddress(detailAddress)) {
                return false;
            }
            if (detailAddress.length() <= SHORT_ADDRESS) {
                return false;
            }
            stringBuilder.append(detailAddress);
        }
        String allLevelAddress = stringBuilder.toString();

        if (StringUtils.isBlank(userInfo.getPhone())) {
            return false;
        } else {
            if (!this.validPhone(userInfo.getPhone().trim())) {
                return false;
            }
        }
        if (this.encryption(allLevelAddress)) {
            return false;
        }
        return true;
    }

    public boolean invalidAddress(String address) {
        Pattern numberPattern = Pattern.compile(NUM_CHAR_SPECIAL_REGULAR);
        return numberPattern.matcher(address).matches();
    }

    public boolean validPhone(String phone) {
        Pattern phonePattern = Pattern.compile(PHONE);
        Pattern mobilePattern = Pattern.compile(MOBILE);
        Pattern phoneExtensionNumber = Pattern.compile(PHONE_EXTENSION_NUMBER);
        return phonePattern.matcher(phone).matches() || mobilePattern.matcher(phone).matches() || phoneExtensionNumber.matcher(phone).matches();
    }

    public boolean encryption(String checkString) {
        checkString.toCharArray();
        if (StringUtils.isNotBlank(checkString)) {
            if (checkString.contains(ENCRYPTION_STRING)) {
                Map<Character, Long> result = checkString.chars().mapToObj(c -> (char) c).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                return result.get(ENCRYPTION_CHAR) >= ENCRYPTION_CHAR_BOTTOM;
            }
        }
        return false;
    }

    private String getPhone(ContactDO refunder) {
        String phone = "";
        if (StringUtils.isNotEmpty(refunder.getPhone()) && this.validPhone(refunder.getPhone().trim())) {
            phone = refunder.getPhone();
        } else if (StringUtils.isNotEmpty(refunder.getMobilePhone()) && this.validPhone(refunder.getMobilePhone().trim())){
            phone = refunder.getMobilePhone();
        } else if(StringUtils.isNotEmpty(refunder.getTelephone()) && this.validPhone(refunder.getTelephone().trim())){
            phone = refunder.getTelephone();
        }
        return phone.trim();
    }
}
