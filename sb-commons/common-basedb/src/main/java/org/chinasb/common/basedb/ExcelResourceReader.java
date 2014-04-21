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
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

/**
 * Execel
 * @author zhujuan
 */
@Component("excelResourceReader")
public class ExcelResourceReader implements ResourceReader {
    private static final Logger logger = LoggerFactory.getLogger(ExcelResourceReader.class);
    private static final String ROW_START = "START";
    private static final String ROW_END = "END";
    private static final TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);

    private ConversionService conversionService = new GenericConversionService();

    @Override
    public String getFormat() {
        return "excel";
    }

    @Override
    public <E> Iterator<E> read(InputStream input, Class<E> clz) {
        Workbook wb = getWorkbook(input);
        Sheet[] sheets = getSheets(wb, clz);
        Collection<FieldInfo> infos = getCellInfos(sheets[0], clz);

        ArrayList<E> result = new ArrayList<E>();
        for (Sheet sheet : sheets) {
            boolean start = false;
            for (Row row : sheet) {
                if (!(start)) {
                    Cell cell = row.getCell(0);
                    if (cell == null) {
                        continue;
                    }
                    String content = getCellContent(cell);
                    if (content == null) {
                        continue;
                    }
                    if (content.equals("SERVER")) {
                        start = true;
                    }
                } else {
                    E instance = newInstance(clz);
                    for (FieldInfo info : infos) {
                        Cell cell = row.getCell(info.index);
                        if (cell == null) {
                            continue;
                        }
                        String content = getCellContent(cell);
                        if (Strings.isNullOrEmpty(content)) {
                            continue;
                        }
                        inject(instance, info.field, content);
                    }
                    result.add(instance);

                    Cell cell = row.getCell(0);
                    if (cell == null) {
                        continue;
                    }
                    String content = getCellContent(cell);
                    if (content == null) {
                        continue;
                    }
                    if (content.equals(ROW_END)) break;
                }
            }
        }
        return result.iterator();
    }

    private String getCellContent(Cell cell) {
        if (cell.getCellType() != Cell.CELL_TYPE_STRING) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        return cell.getStringCellValue();
    }

    private void inject(Object instance, Field field, String content) {
        try {
            TypeDescriptor targetType = new TypeDescriptor(field);
            Object value = this.conversionService.convert(content, sourceType, targetType);
            field.set(instance, value);
        } catch (ConverterNotFoundException e) {
            FormattingTuple message =
                    MessageFormatter.format("静态资源[{}]属性[{}]的转换器不存在", instance.getClass()
                            .getSimpleName(), field.getName());
            logger.error(message.getMessage(), e);
            throw new IllegalStateException(message.getMessage(), e);
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("属性[{}]注入失败", field);
            logger.error(message.getMessage());
            throw new IllegalStateException(message.getMessage(), e);
        }
    }

    private <E> E newInstance(Class<E> clz) {
        try {
            return clz.newInstance();
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("资源[{}]无法实例化", clz);
            logger.error(message.getMessage());
            throw new RuntimeException(message.getMessage());
        }
    }

    private Collection<FieldInfo> getCellInfos(Sheet sheet, Class<?> clz) {
        Row fieldRow = getFieldRow(sheet);
        if (fieldRow == null) {
            FormattingTuple message = MessageFormatter.format("无法获取资源[{}]的EXCEL文件的属性控制列", clz);
            logger.error(message.getMessage());
            throw new IllegalStateException(message.getMessage());
        }

        List<FieldInfo> result = new ArrayList<FieldInfo>();
        for (int i = 1; i < fieldRow.getLastCellNum(); ++i) {
            Cell cell = fieldRow.getCell(i);
            if (cell == null) {
                continue;
            }
            String name = getCellContent(cell);
            if (Strings.isNullOrEmpty(name)) {
                continue;
            }
            try {
                Field field = clz.getDeclaredField(name);
                FieldInfo info = new FieldInfo(i, field);
                result.add(info);
            } catch (Exception e) {
                FormattingTuple message =
                        MessageFormatter.format("资源类[{}]的声明属性[{}]无法获取", clz, name);
                logger.error(message.getMessage());
                throw new IllegalStateException(message.getMessage(), e);
            }
        }
        return result;
    }

    private Row getFieldRow(Sheet sheet) {
        for (Row row : sheet) {
            Cell cell = row.getCell(0);
            if (cell == null) {
                continue;
            }
            String content = getCellContent(cell);
            if ((content != null) && (content.equals(ROW_START))) {
                return row;
            }
        }
        return null;
    }

    private Sheet[] getSheets(Workbook wb, Class<?> clz) {
        try {
            List<Sheet> result = new ArrayList<Sheet>();
            String name = clz.getSimpleName();

            for (int i = 0; i < wb.getNumberOfSheets(); ++i) {
                Sheet sheet = wb.getSheetAt(i);
                if (sheet.getLastRowNum() <= 0) {
                    continue;
                }
                Row row = sheet.getRow(0);
                if (row.getLastCellNum() <= 0) {
                    continue;
                }
                Cell cell = row.getCell(0);
                if (cell == null) {
                    continue;
                }
                if (cell.getCellType() != Cell.CELL_TYPE_STRING) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                }
                String text = cell.getStringCellValue();
                if (name.equals(text)) {
                    result.add(sheet);
                }
            }
            if (result.size() > 0) {
                return ((Sheet[]) result.toArray(new Sheet[0]));
            }

            Sheet sheet = wb.getSheet(name);
            if (sheet != null) {
                return new Sheet[] {sheet};
            }
            return new Sheet[] {wb.getSheetAt(0)};
        } catch (Exception e) {
            throw new IllegalArgumentException("无法获取资源类[" + clz.getSimpleName() + "]对应的Excel数据表", e);
        }
    }

    private Workbook getWorkbook(InputStream input) {
        try {
            return WorkbookFactory.create(input);
        } catch (InvalidFormatException e) {
            throw new RuntimeException("无效的文件格式", e);
        } catch (IOException e) {
            throw new RuntimeException("无法读取文件", e);
        }
    }

    private static class FieldInfo {
        public final int index;
        public final Field field;

        public FieldInfo(int index, Field field) {
            ReflectionUtility.makeAccessible(field);
            this.index = index;
            this.field = field;
        }
    }
}
