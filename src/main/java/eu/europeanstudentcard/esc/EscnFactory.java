package eu.europeanstudentcard.esc;

/**
 * This class is used to generate unique European Student Card Number (ESCN) for the "european student card"
 * <p>
 * This number is an UUID of 16 bytes (128 bits) and the generation algorithm is describe in RFC 4122 :
 * <p>
 * Octet 0-3: time_low The low field of the timestamp
 * <p>
 * Octet 4-5: time_mid The middle field of the timestamp
 * <p>
 * Octet 6-7: time_hi_and_version The high field of the timestamp multiplexed with the version number
 * <p>
 * Octet 8: clock_seq_hi_and_reserved The high field of the clock sequence multiplexed with the variant
 * <p>
 * Octet 9: clock_seq_low The low field of the clock sequence
 * <p>
 * Octet 10-15: node The spatially unique node identifier
 *
 */
public class EscnFactory {
    // Offset from 15 Oct 1582 to 1 Jan 1970
    private static final long OFFSET_MILLIS = 12219292800000L;

    private static long time = 0, oldSysTime = 0;
    private static int clock, hits = 0;

    /**
     * This method calculate an UUID from 2 parameters
     * @param prefix To distinguish servers of a same institution
     * @param pic Participant Identification Code
     * @return a unique ESCN
     */
    public static synchronized String getEscn(Integer prefix, String pic) throws InterruptedException, EscnFactoryException {

        String node = getNode(prefix, pic);

        if (--hits > 0) ++time;
        else {
            long sysTime = System.currentTimeMillis();
            hits = 10000;

            if (sysTime <= oldSysTime) {
                if (sysTime < oldSysTime) {       // SYSTEM CLOCK WAS SET BACK
                    clock = (++clock & 0x3fff) | 0x8000;
                } else {           // REQUESTING UUIDs TOO FAST FOR SYSTEM CLOCK
                    Thread.sleep(1);
                    sysTime = System.currentTimeMillis();
                }
            }

            time = sysTime * 10000L + OFFSET_MILLIS;
            oldSysTime = sysTime;
        }

        int low = (int) time;
        int mid = (int) (time >> 32) & 0xffff;

        // 12 bit hi, set high 4 bits to '0001' for RFC 4122 version 1
        int hi = ((int) (time >> 48) & 0x0fff) | 0x1000;

        return String.format("%08X-%04X-%04X-%04X-%s",
                low, mid, hi, clock, node
        ).toLowerCase();
    }

    /**
     * This method calculate a node used for the ESCN creation and initiate the clock
     * node = Prefix + PIC
     *
     * @param intPrefix To distinguish servers of a same institution
     * @param pic Participant Identification Code
     * @return node
     * @throws "Invalid Prefix format"  if the prefix is malformed
     * @throws "Invalid PIC format"  if the PIC is malformed
     */
    private static String getNode(Integer intPrefix, String pic) throws EscnFactoryException {
        String concatID;
        String prefix;

        prefix = String.format("%03d", intPrefix);

        if (!prefix.matches("[0-9]{3}")) {
            throw new EscnFactoryException("Invalid Prefixe format!");
        }
        if (!pic.matches("[0-9]{9}")) {
            throw new EscnFactoryException("Invalid PIC format!");
        }

        concatID = prefix.concat(pic);


        // 14 bit clock, set high 2 bits to '0001' for RFC 4122 variant 2
        clock = ((int) (Math.random() * 0x3fff)) | 0x8000;

        return concatID;
    }
}