package org.chinasb.common.basedb;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.chinasb.common.utility.ReflectionUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

@Component("excelResourceReader")
public class ExcelResourceReader implements ResourceReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelResourceReader.class);
    private static final TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);

    /**
     * 属性信息
     * @author zhujuan
     */
    private static class FieldInfo {
        public final int index;
        public final Field field;

        public FieldInfo(int index, Field field) {
            ReflectionUtility.makeAccessible(field);
            this.index = index;
            this.field = field;
        }
    }
    
    @Override
    public String getFormat() {
        return "excel";
    }

    @Override
    public <E> Iterator<E> read(InputStream input, Class<E> clz) {
        Workbook wb = getWorkbook(input);
        Sheet sheet = getSheets(wb, clz);
        Collection<FieldInfo> infos = getCellInfos(sheet, clz);
        ArrayList<E> result = new ArrayList<E>();
        for(int i = 1; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            E instance = newInstance(clz);
            for (FieldInfo info : infos) {
                Cell cell = row.getCell(info.index);
                if (cell != null) {
                    String content = getCellContent(cell);
                    if (!Strings.isNullOrEmpty(content)) {
                        inject(instance, info.field, content);
                    }
                }
            }
            result.add(instance);
        }
        return result.iterator();
    }

    /**
     * 获得工作蔳
     * @param input 文件流
     * @return
     */
    private Workbook getWorkbook(InputStream input) {
        try {
            return WorkbookFactory.create(input);
        } catch (InvalidFormatException e) {
            throw new RuntimeException("无效的文件格式", e);
        } catch (IOException e) {
            throw new RuntimeException("无法读取文件", e);
        }
    }
    
    /**
     * 获得工作表格
     * @param wb 工作薄
     * @param clz 基础数据类对象
     * @return
     */
    private Sheet getSheets(Workbook wb, Class<?> clz) {
        Sheet sheet = wb.getSheet(clz.getSimpleName());
        if (sheet != null) {
            return sheet;
        }
        throw new IllegalArgumentException("无法获取资源类[" + clz.getSimpleName() + "]对应的Excel数据表");
    }
    

    /**
     * 获得属性信息
     * @param sheet 工作表格
     * @param clz 基础数据类对象
     * @return
     */
    private Collection<FieldInfo> getCellInfos(Sheet sheet, Class<?> clz) {
        Row fieldRow = getFieldRow(sheet);
        if (fieldRow == null) {
            FormattingTuple message = MessageFormatter.format("无法获取资源[{}]的EXCEL文件的属性控制行", clz);
            LOGGER.error(message.getMessage());
            throw new IllegalStateException(message.getMessage());
        }
        List<FieldInfo> result = new ArrayList<FieldInfo>();
        for (int i = 0; i < fieldRow.getLastCellNum(); i++) {
            Cell cell = fieldRow.getCell(i);
            if (cell != null) {
                String name = getCellContent(cell);
                if (!Strings.isNullOrEmpty(name)) {
                    try {
                        Field field = clz.getDeclaredField(name);
                        FieldInfo info = new FieldInfo(i, field);
                        result.add(info);
                    } catch (Exception e) {
                        FormattingTuple message =
                                MessageFormatter.format("资源类[{}]的声明属性[{}]无法获取", clz, name);
                        LOGGER.error(message.getMessage());
                        throw new IllegalStateException(message.getMessage(), e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获得属性信息行
     * @param sheet 工作表格
     * @return
     */
    private Row getFieldRow(Sheet sheet) {
        for (Row row : sheet) {
            Cell cell = row.getCell(0);
            if (cell != null) {
                String content = getCellContent(cell);
                if ((content != null) && (content.trim().toLowerCase().equals("id"))) {
                    return row;
                }
            }
        }
        return null;
    }
    
    /**
     * 获得单元格内容
     * @param cell 单元格
     * @return
     */
    private String getCellContent(Cell cell) {
        if (cell.getCellType() != Cell.CELL_TYPE_STRING) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        return cell.getStringCellValue();
    }

    /**
     * 创建基础数据实例
     * @param clz 基础数据类对象
     * @return
     */
    private <E> E newInstance(Class<E> clz) {
        try {
            return clz.newInstance();
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("资源[{}]无法实例化", clz);
            LOGGER.error(message.getMessage());
            throw new RuntimeException(message.getMessage());
        }
    }
    
    private GenericConversionService conversionService = new DefaultConversionService();

    /**
     * 设置属性
     * @param instance
     * @param field
     * @param content
     */
    private void inject(Object instance, Field field, String content) {
        try {
            TypeDescriptor targetType = new TypeDescriptor(field);
            Object value = conversionService.convert(content, sourceType, targetType);
            field.set(instance, value);
        } catch (ConverterNotFoundException e) {
            FormattingTuple message =
                    MessageFormatter.format("静态资源[{}]属性[{}]的转换器不存在", instance.getClass()
                            .getSimpleName(), field.getName());
            LOGGER.error(message.getMessage(), e);
            throw new IllegalStateException(message.getMessage(), e);
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("属性[{}]注入失败", field);
            LOGGER.error(message.getMessage());
            throw new IllegalStateException(message.getMessage(), e);
        }
    }
}
