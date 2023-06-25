package lsj.oauth2.resource;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class AddressUtil {
    public static final String SPLITTER1 = "~~~";
    public static final String SPLITTER1_REGEX = "~~~";
    public static final String SPLITTER2 = "^^^";
    public static final String SPLITTER2_REGEX = "\\^\\^\\^";
    public static final String COUNTRY = "country";
    public static final String PROVINCE = "province";
    public static final String CITY = "city";
    public static final String DISTRICT = "district";
    public static final String STREET = "street";

    // 跟LC保持一致
    public static final String ADDRESS_DETAIL_SEPERATOR = "   ";
    private static final String[] NAME_INFO = {COUNTRY, PROVINCE, CITY, DISTRICT, STREET};


    public static Address parse(String address) {
        Map<String, String> addressMap = new HashMap<String, String>();
        if (address == null) {
            return null;
        }
        try {
            address = padding(address);
            String splitter = decideSplitter(address);
            String splitterRegex = decideSplitterRegex(address);
            String[] addInfo = address.split(splitterRegex);
            if (SPLITTER1.equals(splitter)) {
                for (int i = 0; i < 3 && i < addInfo.length; i++) {
                    addressMap.put(NAME_INFO[i + 1], addInfo[i].trim());
                }
                StringBuffer sb = new StringBuffer();
                for (int i = 3; i < addInfo.length; i++) {
                    sb.append(addInfo[i].trim());
                    sb.append(splitter);
                }
                if (sb.length() > 0) {
                    addressMap.put(NAME_INFO[4], sb.substring(0, sb.lastIndexOf(splitter)));
                }
            } else if (SPLITTER2.equals(splitter)) {
                for (int i = 0; i < 4 && i < addInfo.length; i++) {
                    addressMap.put(NAME_INFO[i], addInfo[i].trim());
                }
                StringBuffer sb = new StringBuffer();
                for (int i = 4; i < addInfo.length; i++) {
                    sb.append(addInfo[i].trim());
                    sb.append(splitter);
                }
                if (sb.length() > 0) {
                    addressMap.put(NAME_INFO[4], sb.substring(0, sb.lastIndexOf(splitter)));
                }
            }
        } catch (Exception e) {
            return null;
        }

        Address addressDTO = new Address();
        addressDTO.setCountry(addressMap.get(AddressUtil.COUNTRY));
        addressDTO.setProvince(addressMap.get(AddressUtil.PROVINCE));
        addressDTO.setCity(addressMap.get(AddressUtil.CITY));
        addressDTO.setDistrict(addressMap.get(AddressUtil.DISTRICT));
        addressDTO.setDetailAddress(addressMap.get(AddressUtil.STREET));
        return addressDTO;
    }

    private static final String decideSplitter(String address) {
        if (address.contains(SPLITTER1)) {
            return SPLITTER1;
        } else if (address.contains(SPLITTER2)) {
            return SPLITTER2;
        }
        return SPLITTER1;
    }

    private static final String decideSplitterRegex(String address) {
        if (address.contains(SPLITTER1)) {
            return SPLITTER1_REGEX;
        } else if (address.contains(SPLITTER2)) {
            return SPLITTER2_REGEX;
        }
        return SPLITTER1_REGEX;
    }

    private static String padding(String address) {
        String result = address;
        if (result.startsWith(SPLITTER1)) {
            result = " " + address;
        } else if (result.startsWith(SPLITTER2)) {
            result = " " + address;
        }

        if (result.endsWith(SPLITTER1)) {
            result = result + " ";
        } else if (result.endsWith(SPLITTER2)) {
            result = result + " ";
        }
        return result;
    }

    public static void main(String[] args) {
        Address address = AddressUtil.parse("Россия^^^Краснодарский край^^^Ленинский^^^^^^Еврейская-Ленинский");
        Address address2 = AddressUtil.parse("俄罗斯^^^Москва^^^Зеленоград^^^ ^^^   корпус 405 , кв.120");


        System.out.println(address);
    }
}
