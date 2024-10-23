package io.github.yaojiqunaer.commons.lang;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

/**
 * KiB:千字节,1KB = 1024字节
 * MiB:百万字节,1MB = 1024*1024 字节
 * GiB:十亿字节,1GB = 1024*1024*1024字节
 * TiB:万亿字节,1TB = 1024*1024*1024*1024字节
 * PiB:百万亿字节,1PB = 1024*1024*1024*1024*1024字节
 * EiB:百万亿字节,1PB = 1024*1024*1024*1024*1024*1024字节
 */
public enum ByteUnit {

    /**
     * 字节
     */
    B {
        @Override
        public long toB(Number number) {
            return number.longValue();
        }

        @Override
        public long toKi(Number number) {
            return number.longValue() / (1L << 10);
        }

        @Override
        public long toMi(Number number) {
            return number.longValue() / (1L << 20);
        }

        @Override
        public long toGi(Number number) {
            return number.longValue() / (1L << 30);
        }

        @Override
        public long toTi(Number number) {
            return number.longValue() / (1L << 40);
        }

        @Override
        public long toPi(Number number) {
            return number.longValue() / (1L << 50);
        }

        @Override
        public long toEi(Number number) {
            return number.longValue() / (1L << 60);
        }

        @Override
        public String format(Number number) {
            long value = Math.abs(number.longValue());
            if (number.doubleValue() == 0) {
                return "0B";
            }
            int digitGroups = (int) (Math.log10(value) / Math.log10(1024));
            return String.format("%s%s%s", (number.doubleValue() < 0 ? "-" : ""),
                    new DecimalFormat("#,##0.##").format(value / Math.pow(1024, digitGroups)), BYTE_UNITS[digitGroups]);
        }
    },
    KI {
        @Override
        public long toB(Number number) {
            return number.longValue() * (1L << 10);
        }

        @Override
        public long toKi(Number number) {
            return number.longValue();
        }

        @Override
        public long toMi(Number number) {
            return number.longValue() / (1L << 10);
        }

        @Override
        public long toGi(Number number) {
            return number.longValue() / (1L << 20);
        }

        @Override
        public long toTi(Number number) {
            return number.longValue() / (1L << 30);
        }

        @Override
        public long toPi(Number number) {
            return number.longValue() / (1L << 40);
        }

        @Override
        public long toEi(Number number) {
            return number.longValue() / (1L << 50);
        }
    },
    MI {
        @Override
        public long toB(Number number) {
            return number.longValue() * (1L << 20);
        }

        @Override
        public long toKi(Number number) {
            return number.longValue() * (1L << 10);
        }

        @Override
        public long toMi(Number number) {
            return number.longValue();
        }

        @Override
        public long toGi(Number number) {
            return number.longValue() / (1L << 10);
        }

        @Override
        public long toTi(Number number) {
            return number.longValue() / (1L << 20);
        }

        @Override
        public long toPi(Number number) {
            return number.longValue() / (1L << 30);
        }

        @Override
        public long toEi(Number number) {
            return number.longValue() / (1L << 40);
        }
    },
    GI {
        @Override
        public long toB(Number number) {
            return number.longValue() * (1L << 30);
        }

        @Override
        public long toKi(Number number) {
            return number.longValue() * (1L << 20);
        }

        @Override
        public long toMi(Number number) {
            return number.longValue() * (1L << 10);
        }

        @Override
        public long toGi(Number number) {
            return number.longValue();
        }

        @Override
        public long toTi(Number number) {
            return number.longValue() / (1L << 10);
        }

        @Override
        public long toPi(Number number) {
            return number.longValue() / (1L << 20);
        }

        @Override
        public long toEi(Number number) {
            return number.longValue() / (1L << 30);
        }
    },
    TI {
        @Override
        public long toB(Number number) {
            return number.longValue() * (1L << 40);
        }

        @Override
        public long toKi(Number number) {
            return number.longValue() * (1L << 30);
        }

        @Override
        public long toMi(Number number) {
            return number.longValue() * (1L << 20);
        }

        @Override
        public long toGi(Number number) {
            return number.longValue() * (1L << 10);
        }

        @Override
        public long toTi(Number number) {
            return number.longValue();
        }

        @Override
        public long toPi(Number number) {
            return number.longValue() / (1L << 10);
        }

        @Override
        public long toEi(Number number) {
            return number.longValue() / (1L << 20);
        }
    },
    PI {
        @Override
        public long toB(Number number) {
            return number.longValue() * (1L << 50);
        }

        @Override
        public long toKi(Number number) {
            return number.longValue() * (1L << 40);
        }

        @Override
        public long toMi(Number number) {
            return number.longValue() * (1L << 30);
        }

        @Override
        public long toGi(Number number) {
            return number.longValue() * (1L << 20);
        }

        @Override
        public long toTi(Number number) {
            return number.longValue() * (1L << 10);
        }

        @Override
        public long toPi(Number number) {
            return number.longValue();
        }

        @Override
        public long toEi(Number number) {
            return number.longValue() / (1L << 10);
        }
    },
    EI {
        @Override
        public long toB(Number number) {
            return number.longValue() * (1L << 60);
        }

        @Override
        public long toKi(Number number) {
            return number.longValue() * (1L << 50);
        }

        @Override
        public long toMi(Number number) {
            return number.longValue() * (1L << 40);
        }

        @Override
        public long toGi(Number number) {
            return number.longValue() * (1L << 30);
        }

        @Override
        public long toTi(Number number) {
            return number.longValue() * (1L << 20);
        }

        @Override
        public long toPi(Number number) {
            return number.longValue() * (1 << 10);
        }

        @Override
        public long toEi(Number number) {
            return number.longValue();
        }
    };

    private static final String EXCEPTION_MSG = "method is not implements";
    private static final String[] BYTE_UNITS = new String[]{"B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB"};

    /**
     * 将单位换算为B
     *
     * @param number 任意数字
     * @return 转换单位后的数据
     */
    public long toB(Number number) {
        throw new AbstractMethodError();
    }

    /**
     * 将单位换算为KiB
     *
     * @param number 任意数字
     * @return 转换单位后的数据
     */
    public long toKi(Number number) {
        throw new AbstractMethodError(EXCEPTION_MSG);
    }

    /**
     * 将单位换算为MiB
     *
     * @param number 任意数字
     * @return 转换单位后的数据
     */
    public long toMi(Number number) {
        throw new AbstractMethodError(EXCEPTION_MSG);
    }

    /**
     * 将单位换算为GiB
     *
     * @param number 任意数字
     * @return 转换单位后的数据
     */
    public long toGi(Number number) {
        throw new AbstractMethodError(EXCEPTION_MSG);
    }

    /**
     * 将单位换算为TiB
     *
     * @param number 任意数字
     * @return 转换单位后的数据
     */
    public long toTi(Number number) {
        throw new AbstractMethodError(EXCEPTION_MSG);
    }

    /**
     * 将单位换算为PiB
     *
     * @param number 任意数字
     * @return 转换单位后的数据
     */
    public long toPi(Number number) {
        throw new AbstractMethodError(EXCEPTION_MSG);
    }

    /**
     * 将单位换算为EiB
     *
     * @param number 任意数字
     * @return 转换单位后的数据
     */
    public long toEi(Number number) {
        throw new AbstractMethodError(EXCEPTION_MSG);
    }

    /**
     * 自动格式化
     *
     * @param number 任意数字
     * @return 转换为自适应可读格式化结果
     */
    public String format(Number number) {
        throw new AbstractMethodError(EXCEPTION_MSG);
    }

    public static ByteUnit of(String unit) {
        if (StringUtils.containsIgnoreCase(unit, "Ki")) {
            return KI;
        } else if (StringUtils.containsIgnoreCase(unit, "Mi")) {
            return MI;
        } else if (StringUtils.containsIgnoreCase(unit, "Gi")) {
            return GI;
        } else if (StringUtils.containsIgnoreCase(unit, "Ti")) {
            return TI;
        } else if (StringUtils.containsIgnoreCase(unit, "Pi")) {
            return PI;
        } else if (StringUtils.containsIgnoreCase(unit, "Ei")) {
            return EI;
        } else {
            return B;
        }
    }

}
