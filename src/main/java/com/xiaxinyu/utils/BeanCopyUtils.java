package com.xiaxinyu.utils;

import com.xiaxinyu.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 对象拷贝
 */
@Slf4j
public class BeanCopyUtils {
    /**
     * 集合对象拷贝
     * targetClass 必须有无参的构造方法，或者newInstance重新后也是无参的
     *
     * @param sourceList  源集合
     * @param targetClass 拷贝对象
     * @param <T>         类型
     * @return 对应类型的集合
     */
    public static <T> List<T> copyList(List sourceList, Class<T> targetClass) {


        if (CollectionUtils.isEmpty(sourceList) || Objects.isNull(targetClass)) {
            return new ArrayList<>(0);
        }
        List<T> result = new ArrayList<>(sourceList.size());
        try {
            for (Object source : sourceList) {

                T t = targetClass.newInstance();
                BeanUtils.copyProperties(source, t);
                result.add(t);
            }
        } catch (Exception e) {

            throw new ApplicationException("copy.init.error", targetClass.getName());
        }
        return result;
    }


    public static <T> T copy(@NotNull Object source, @NotNull Class<T> targetClass) {

        try {

            T t = targetClass.newInstance();
            if (Objects.isNull(source)) {
                return t;
            }
            BeanUtils.copyProperties(source, t);
            return t;
        } catch (Exception e) {
            throw new ApplicationException("copy.init.error", targetClass.getName());
        }
    }

    /**
     * 集合按照500分组
     *
     * @param source
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> group(List<T> source) {

        return group(source, 500);

    }

    public static <T> List<List<T>> group(List<T> source, int groupNum) {
        if (CollectionUtils.isEmpty(source)) {
            return new ArrayList<>();
        }

        int size = source.size();
        int length = size / groupNum + 1;
        List<List<T>> group = new ArrayList<>(length);
        List<T> node = null;
        int start = 0;
        for (int i = 0; i < length; i++) {
            start = i * groupNum;
            //整除的最后一个不算
            if (start == size) {
                break;
            }
            node = new ArrayList<>(source.subList(start, Math.min(size, (i + 1) * groupNum)));
            group.add(node);
        }
        return group;
    }
}
