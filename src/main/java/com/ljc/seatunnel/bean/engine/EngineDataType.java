package com.ljc.seatunnel.bean.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.seatunnel.api.table.type.*;

public class EngineDataType {

    public static DataType T_STRING = new DataType("string", BasicType.STRING_TYPE);
    public static DataType T_BOOLEAN = new DataType("boolean", BasicType.BOOLEAN_TYPE);
    public static DataType T_BYTE = new DataType("tinyint", BasicType.BYTE_TYPE);
    public static DataType T_SHORT = new DataType("smallint", BasicType.SHORT_TYPE);
    public static DataType T_INT = new DataType("int", BasicType.INT_TYPE);
    public static DataType T_LONG = new DataType("bigint", BasicType.LONG_TYPE);
    public static DataType T_FLOAT = new DataType("float", BasicType.FLOAT_TYPE);
    public static DataType T_DOUBLE = new DataType("double", BasicType.DOUBLE_TYPE);
    public static DataType T_VOID = new DataType("null", BasicType.VOID_TYPE);

    public static DataType T_DECIMAL = new DataType("decimal(38, 18)", new DecimalType(38, 18));

    public static DataType T_LOCAL_DATE = new DataType("date", LocalTimeType.LOCAL_DATE_TYPE);
    public static DataType T_LOCAL_TIME = new DataType("time", LocalTimeType.LOCAL_TIME_TYPE);
    public static DataType T_LOCAL_DATE_TIME =
            new DataType("timestamp", LocalTimeType.LOCAL_DATE_TIME_TYPE);

    public static DataType T_PRIMITIVE_BYTE_ARRAY =
            new DataType("bytes", PrimitiveByteArrayType.INSTANCE);

    public static DataType T_STRING_ARRAY =
            new DataType("array<string>", ArrayType.STRING_ARRAY_TYPE);
    public static DataType T_BOOLEAN_ARRAY =
            new DataType("array<boolean>", ArrayType.BOOLEAN_ARRAY_TYPE);
    public static DataType T_BYTE_ARRAY = new DataType("array<tinyint>", ArrayType.BYTE_ARRAY_TYPE);
    public static DataType T_SHORT_ARRAY =
            new DataType("array<smallint>", ArrayType.SHORT_ARRAY_TYPE);
    public static DataType T_INT_ARRAY = new DataType("array<int>", ArrayType.INT_ARRAY_TYPE);
    public static DataType T_LONG_ARRAY = new DataType("array<bigint>", ArrayType.LONG_ARRAY_TYPE);
    public static DataType T_FLOAT_ARRAY = new DataType("array<float>", ArrayType.FLOAT_ARRAY_TYPE);
    public static DataType T_DOUBLE_ARRAY =
            new DataType("array<double>", ArrayType.DOUBLE_ARRAY_TYPE);

    @Data
    @AllArgsConstructor
    public static class DataType {
        String name;
        SeaTunnelDataType<?> RawType;
    }
}
