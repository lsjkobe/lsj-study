package lsj.oauth2.resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:wushuangquan.wsq@cainiao.com">wushuangquan.wsq</a>
 * @version 1.0
 * @since 2021年08月17日 17：38
 */
@Service
public class AddressUtils {

    public static final String START_CHINA = "中国^^^";

    public static final String CHINA_START = "中国＾＾＾";


    public static Address parseAddressFromLC(String address) {
        Address finalAddress = new Address();
        if (StringUtils.isBlank(address)) {
            return finalAddress;
        }
        if (address.split("\\^{12}").length > 1) {
            address = address.split("\\^{12}")[1];
        }
        if (address.startsWith(CHINA_START)) {
            address = address.replace(CHINA_START, "");
        }
        if (address.startsWith(START_CHINA)) {
            address = address.replace(START_CHINA, "");
        }
        if (address.contains("^")) {
            address = address.replace("^", "~");
        }
        if (address.contains("＾")) {
            address = address.replace("＾", "~");
        }
        finalAddress = AddressUtil.parse(address);
        String detailAddress = finalAddress.getDetailAddress();
        //解析工具解析出来的 街道地址可能为空，详细地址可能包含街道，以下逻辑会尝试根据解析出来的详细地址解析街道和准确详细地址
        if (org.apache.commons.lang3.StringUtils.isNotBlank(detailAddress)) {
            int detailAddressSplitPoint = detailAddress.indexOf("~");
            if (org.apache.commons.lang3.StringUtils.isNotBlank(detailAddress)) {
                if (org.apache.commons.lang3.StringUtils.isBlank(finalAddress.getStreet())) {
                    if (detailAddressSplitPoint > 0) {
                        finalAddress.setStreet(detailAddress.substring(0, detailAddressSplitPoint));
                    }
                }
                if (detailAddressSplitPoint > 0) {
                    finalAddress.setDetailAddress(detailAddress.substring(detailAddress.lastIndexOf("~") + 1));
                }
            }
        }
        return finalAddress;
    }


    private static String getSeparator(String rawAddress) {
        if (rawAddress.contains("^")) {
            return "^";
        }
        if (rawAddress.contains("/")) {
            return "/";
        }
        if (rawAddress.contains("＾")) {
            return "＾";
        }
        return "";
    }
}
