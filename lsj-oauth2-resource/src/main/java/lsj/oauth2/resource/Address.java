package lsj.oauth2.resource;

public class Address {
    /**
     * 国家
     */
    private String country;

    /**
     * 国家
     */
    private String countryCode;

    /**
     * 省、直辖市、州
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区、县
     */
    private String district;

    /**
     * 乡镇、街道
     */
    private String street;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 获取country
     *
     * @return country
     */
    public String getCountry() {
        return country;
    }

    /**
     * 设置country
     *
     * @param country 要设置的country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 获取countryCode
     *
     * @return countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * 设置countryCode
     *
     * @param countryCode 要设置的countryCode
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * 获取province
     *
     * @return province
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置province
     *
     * @param province 要设置的province
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 获取city
     *
     * @return city
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置city
     *
     * @param city 要设置的city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取district
     *
     * @return district
     */
    public String getDistrict() {
        return district;
    }

    /**
     * 设置district
     *
     * @param district 要设置的district
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * 获取street
     *
     * @return street
     */
    public String getStreet() {
        return street;
    }

    /**
     * 设置street
     *
     * @param street 要设置的street
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * 获取detailAddress
     *
     * @return detailAddress
     */
    public String getDetailAddress() {
        return detailAddress;
    }

    /**
     * 设置detailAddress
     *
     * @param detailAddress 要设置的detailAddress
     */
    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }
}
