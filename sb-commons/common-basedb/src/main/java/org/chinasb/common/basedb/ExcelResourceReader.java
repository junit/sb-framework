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
import org.chinasb.common.utility.ReflectionHelper;
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
    /** 服务端控制标识同时也是数据开始标识 */
    public final static String ROW_SERVER = "SERVER";
    /** 客户端控制标识同时也是数据开始标识 */
    public final static String ROW_CLIENT = "CLIENT";
    /** 结束标识 */
    public final static String ROW_END = "END";

    @Override
    public String getFormat() {
        return "excel";
    }

    /**
     * 属性信息
     * 
     * @author zhujuan
     */
    private static class FieldInfo {
        /** 第几列 */
        public final int index;
        /** 资源类属性 */
        public final Field field;

        /** 构造方法 */
        public FieldInfo(int index, Field field) {
            ReflectionHelper.makeAccessible(field);
            this.index = index;
            this.field = field;
        }
    }

    @Override
    public <E> Iterator<E> read(InputStream input, Class<E> clz) {
        // 基本信息获取
        Workbook wb = getWorkbook(input, clz);
        Sheet[] sheets = getSheets(wb, clz);
        Collection<FieldInfo> infos = getCellInfos(sheets[0], clz);

        // 创建返回数据集
        ArrayList<E> result = new ArrayList<E>();
        for (Sheet sheet : sheets) {
            boolean start = false;
            for (Row row : sheet) {
                // 判断数据行开始没有
                if (!start) {
                    Cell cell = row.getCell(0);
                    if (cell == null) {
                        continue;
                    }
                    String content = getCellContent(cell);
                    if (content == null) {
                        continue;
                    }
                    if (content.equals(ROW_SERVER)) {
                        start = true;
                    }
                    continue;
                }
                // 生成返回对象
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

                // 结束处理
                Cell cell = row.getCell(0);
                if (cell == null) {
                    continue;
                }
                String content = getCellContent(cell);
                if (content == null) {
                    continue;
                }
                if (content.equals(ROW_END)) {
                    break;
                }
            }
        }
        return result.iterator();
    }

    /**
     * 通过输入流获取{@link Workbook}
     * 
     * @param input
     * @param clz
     * @return
     */
    private Workbook getWorkbook(InputStream input, @SuppressWarnings("rawtypes") Class clz) {
        try {
            return WorkbookFactory.create(input);
        } catch (InvalidFormatException e) {
            throw new RuntimeException("静态资源[" + clz.getSimpleName() + "]异常,无效的文件格式", e);
        } catch (IOException e) {
            throw new RuntimeException("静态资源[" + clz.getSimpleName() + "]异常,无法读取文件", e);
        }
    }

    /**
     * 获取资源类型对应的工作表格
     * 
     * @param wb 工作薄
     * @param clz 资源类型
     * @return
     */
    private Sheet[] getSheets(Workbook wb, Class<?> clz) {
        try {
            List<Sheet> result = new ArrayList<Sheet>();
            String name = clz.getSimpleName();
            // 处理多Sheet数据合并
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
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
                return result.toArray(new Sheet[result.size()]);
            }

            // 没有需要多Sheet合并的情况
            Sheet sheet = wb.getSheet(name);
            if (sheet != null) {
                return new Sheet[] {sheet};
            } else {
                return new Sheet[] {wb.getSheetAt(0)};
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("无法获取资源类[" + clz.getSimpleName() + "]对应的Excel数据表", e);
        }
    }

    /**
     * 获取表格信息
     * 
     * @param sheet
     * @param clz
     * @return
     */
    private Collection<FieldInfo> getCellInfos(Sheet sheet, Class<?> clz) {
        // 获取属性控制行
        Row fieldRow = getFieldRow(sheet);
        if (fieldRow == null) {
            FormattingTuple message = MessageFormatter.format("无法获取资源[{}]的EXCEL文件的属性控制行", clz);
            LOGGER.error(message.getMessage());
            throw new IllegalStateException(message.getMessage());
        }

        // 获取属性信息集合
        List<FieldInfo> result = new ArrayList<FieldInfo>();
        for (int i = 1; i < fieldRow.getLastCellNum(); i++) {
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
                LOGGER.error(message.getMessage());
                throw new IllegalStateException(message.getMessage(), e);
            }
        }
        return result;
    }

    /**
     * 获取属性控制行
     * 
     * @param sheet
     * @return
     */
    private Row getFieldRow(Sheet sheet) {
        for (Row row : sheet) {
            Cell cell = row.getCell(0);
            if (cell == null) {
                continue;
            }
            String content = getCellContent(cell);
            if (content != null && content.equals(ROW_SERVER)) {
                return row;
            }
        }
        return null;
    }

    /**
     * 获取字符串形式的单元格内容
     * 
     * @param cell
     * @return
     */
    private String getCellContent(Cell cell) {
        if (cell.getCellType() != Cell.CELL_TYPE_STRING) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        return cell.getStringCellValue();
    }

    /**
     * 实例化资源
     * 
     * @param clz
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
     * 给实例注入属性
     * 
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
