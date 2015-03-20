package org.chinasb.common.utility;


/**
 * 由来：FNV哈希算法全名为Fowler-Noll-Vo算法，是以三位发明人Glenn Fowler，Landon Curt Noll，Phong Vo的名字来命名的，最早在1991年提出。
 * 特点和用途：FNV能快速hash大量数据并保持较小的冲突率，它的高度分散使它适用于hash一些非常相近的字符串，比如URL，hostname，文件名，text，IP地址等。
 * 算法版本：FNV算法有三个版本：FNV-0（已废弃）、FNV-1和FNV-1a
 * FNV-1和FNV-1a算法对于最终生成的哈希值（hash）有一定限制
 * 
 *　1，hash是无符号整型
 *　2，hash的位数（bits），应该是2的n次方（32，64，128，256，512，1024），一般32位的就够用了。
 * 
 * 算法描述：
 *     相关变量：
 *         hash值：一个n位的unsigned int型hash值
 *         offset_basis：初始的哈希值
 *         FNV_prime：FNV用于散列的质数
 *         octet_of_data：8位数据（即一个字节）
 *     FNV-1描述：
 *         hash = offset_basis
 *         for each octet_of_data to be hashed
 *             hash = hash * FNV_prime
 *             hash = hash xor octet_of_data
 *         return hash
 *     FNV-1a描述：
 *         hash = offset_basis 
 *         for each octet_of_data to be hashed
 *             hash = hash xor octet_of_data
 *             hash = hash * FNV_prime
 *         return hash
 * 
 * FNV-1a和FNV-1的唯一区别就是xor和multiply的顺序不同，他们所采用的FNV_prime和offset_basis都相同，有人认为FNV-1a在进行小数据（小于4个字节）哈希时有更好的性能。
 * for each octet_of_data to be hashed 意思是对于你要算哈希值的数，它的每一个字节。
 * hash = hash * FNV_prime，是包含取模运算的，具体看你采用多少位的哈希函数。例如，你用32为哈希，hash = hash * FNV_prime % （2的32次方）；
 * hash = hash xor octet_of_data，意思是把当前取来的字节和当前的hash值的第八位做抑或运算。
 * 
 * FNV_prime的取值: 
 * 32 bit FNV_prime = 2^24 + 2^8 + 0x93 = 16777619
 * 64 bit FNV_prime = 2^40 + 2^8 + 0xb3 = 1099511628211
 * 128 bit FNV_prime = 2^88 + 2^8 + 0x3b = 309485009821345068724781371
 * 256 bit FNV_prime = 2^168 + 2^8 + 0x63 =374144419156711147060143317175368453031918731002211
 * 512 bit FNV_prime = 2^344 + 2^8 + 0x57 = 35835915874844867368919076489095108449946327955754392558399825615420669938882575
 * 126094039892345713852759
 * 1024 bit FNV_prime = 2^680 + 2^8 + 0x8d = 
 * 50164565101131186554345988110352789550307653454047907443030175238311120551081474
 * 51509157692220295382716162651878526895249385292291816524375083746691371804094271
 * 873160484737966720260389217684476157468082573
 * 
 * offset_basis的取值: 
 * 32 bit offset_basis = 2166136261
 * 64 bit offset_basis = 14695981039346656037
 * 128 bit offset_basis = 144066263297769815596495629667062367629
 * 256 bit offset_basis = 100029257958052580907070968620625704837092796014241193945225284501741471925557
 * 512 bit offset_basis = 96593031294966694980094354007163104660904187456726378961083743294344626579945829
 * 32197716438449813051892206539805784495328239340083876191928701583869517785
 * 1024 bit offset_basis = 14197795064947621068722070641403218320880622795441933960878474914617582723252296
 * 73230371772215086409652120235554936562817466910857181476047101507614802975596980
 * 40773201576924585630032153049571501574036444603635505054127112859663616102678680
 * 82893823963790439336411086884584107735010676915
 * 
 *
 */
public class HashUtility {
    
    /** Initial seed for 32-bit hashes. */
    public static final long FNV1_32_INIT = 0x811c9dc5L;
    /** Initial seed for 64-bit hashes. */
    public static final long FNV1_64_INIT = 0xcbf29ce484222325L;

    /**
     * limited to the lower 32 bits.
     * 
     * @param buf
     * @param offset
     * @param len
     * @return
     */
    public static long fnv32(byte[] buf, int offset, int len) {
        long seed = FNV1_32_INIT;
        for (int i = offset; i < offset + len; i++) {
            seed += (seed << 1) + (seed << 4) + (seed << 7) + (seed << 8) + (seed << 24);
            seed ^= buf[i];
        }
        return (seed & 0x00000000ffffffffL);
    }

    /**
     * limited to the lower 32 bits.
     * 
     * @param buf
     * @param offset
     * @param len
     * @return
     */
    public long fnv32a(byte[] buf, int offset, int len) {
        long seed = FNV1_32_INIT;
        for (int i = offset; i < offset + len; i++) {
            seed ^= buf[i];
            seed += (seed << 1) + (seed << 4) + (seed << 7) + (seed << 8) + (seed << 24);
        }
        return (seed & 0x00000000ffffffffL);
    }

    /**
     * limited to the lower 64 bits.
     * 
     * @param buf
     * @param offset
     * @param len
     * @return
     */
    public long fnv64(byte[] buf, int offset, int len) {
        long seed = FNV1_64_INIT;
        for (int i = offset; i < offset + len; i++) {
            seed +=
                    (seed << 1) + (seed << 4) + (seed << 5) + (seed << 7) + (seed << 8)
                            + (seed << 40);
            seed ^= buf[i];
        }
        return seed;
    }

    /**
     * limited to the lower 64 bits.
     * 
     * @param buf
     * @param offset
     * @param len
     * @return
     */
    public long fnv64a(byte[] buf, int offset, int len) {
        long seed = FNV1_64_INIT;
        for (int i = offset; i < offset + len; i++) {
            seed ^= buf[i];
            seed +=
                    (seed << 1) + (seed << 4) + (seed << 5) + (seed << 7) + (seed << 8)
                            + (seed << 40);
        }
        return seed;
    }
}
