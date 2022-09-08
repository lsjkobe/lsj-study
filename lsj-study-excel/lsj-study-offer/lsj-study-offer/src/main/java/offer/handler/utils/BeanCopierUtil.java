package offer.handler.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author liuya
 * @description 对象拷贝
 * @date 2019/11/22 13:51
 */
public class BeanCopierUtil {
    public static <T> T toNewObject(Object source, Class<T> targetType) {
        return toNewObject(source, targetType, true);
    }

    public static <T> T toNewObject(Object source, Class<T> targetType, boolean useConverter) {
        if (source == null) {
            return null;
        }
        T target = null;
        try {
            target = targetType.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("create class[" + targetType.getName()
                    + "] instance error", e);
        }
        BeanCopier beanCopier = BeanCopier.create(source.getClass(), targetType, true);
        Converter converter = new CopyConverter();
        beanCopier.copy(source, target, converter);
        return target;
    }

    public static <T> List<T> toNewObjects(Collection sources, Class<T> targetType) {
        return toNewObjects(sources, targetType, true);
    }

    public static <T> List<T> toNewObjects(Collection sources, Class<T> targetType, boolean useConverter) {
        if (sources == null || sources.isEmpty()) {
            return new ArrayList<T>();
        }
        ArrayList<T> targets = new ArrayList<>(10000);
        BeanCopier beanCopier = BeanCopier.create(sources.iterator().next().getClass(), targetType, useConverter);
        Converter converter = new CopyConverter();
        for (Object source : sources) {
            T target = null;
            try {
                target = targetType.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("create class[" + targetType.getName()
                        + "] instance error", e);
            }
            beanCopier.copy(source, target, converter);
            targets.add(target);
        }
        return targets;
    }

    public static class CopyConverter implements Converter {
        @Override
        public Object convert(Object value, Class target, Object context) {
            if(value == null){
                return value;
            }
            String s = value.toString();
            if (target.equals(int.class) || target.equals(Integer.class)) {
                return Integer.parseInt(s);
            }
            if (target.equals(long.class) || target.equals(Long.class)) {
                return Long.parseLong(s);
            }
            if (target.equals(float.class) || target.equals(Float.class)) {
                return Float.parseFloat(s);
            }
            if (target.equals(double.class) || target.equals(Double.class)) {
                return Double.parseDouble(s);
            }
            if (target.equals(Date.class)) {
                long time = ((Date)value).getTime();
                return new Date(time);
            }
            if (target.equals(BigDecimal.class)) {
                if (!StringUtils.isEmpty(s) && !s.equals("NaN")) {
                    return new BigDecimal(s);
                }
            }
            return value;
        }
    }
}

