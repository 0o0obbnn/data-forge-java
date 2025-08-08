package com.dataforge.generators.network;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 网络设备生成器
 * 生成各种网络设备标识符和配置信息
 */
public class NetworkDeviceGenerator implements DataGenerator<String> {
    
    private static final Random random = new Random();
    
    private final DeviceType deviceType;
    private final DeviceFormat format;
    private final boolean includeVendorInfo;
    
    public enum DeviceType {
        MAC_ADDRESS,        // MAC地址
        DEVICE_ID,          // 设备ID
        SERIAL_NUMBER,      // 序列号
        HOSTNAME,           // 主机名
        INTERFACE_NAME,     // 接口名称
        VLAN_ID,            // VLAN ID
        SSID,               // WiFi网络名
        BSSID,              // WiFi基站标识
        DEVICE_NAME         // 设备名称
    }
    
    public enum DeviceFormat {
        STANDARD,           // 标准格式
        CISCO,              // Cisco格式
        HUAWEI,            // 华为格式
        WINDOWS,           // Windows格式
        LINUX,             // Linux格式
        ANDROID,           // Android格式
        IOS                // iOS格式
    }
    
    // IEEE OUI (Organizationally Unique Identifier) 前缀
    private static final Map<String, String> VENDOR_OUI_MAP = new HashMap<>();
    
    static {
        // 主要厂商的OUI前缀
        VENDOR_OUI_MAP.put("Apple", "00:1B:63");
        VENDOR_OUI_MAP.put("Cisco", "00:1E:14");
        VENDOR_OUI_MAP.put("Huawei", "00:E0:FC");
        VENDOR_OUI_MAP.put("Samsung", "00:16:32");
        VENDOR_OUI_MAP.put("Intel", "00:15:00");
        VENDOR_OUI_MAP.put("Dell", "00:14:22");
        VENDOR_OUI_MAP.put("HP", "00:1F:29");
        VENDOR_OUI_MAP.put("Lenovo", "00:21:CC");
        VENDOR_OUI_MAP.put("ASUS", "00:1D:60");
        VENDOR_OUI_MAP.put("Xiaomi", "34:CE:00");
        VENDOR_OUI_MAP.put("TP-Link", "50:C7:BF");
        VENDOR_OUI_MAP.put("D-Link", "00:05:5D");
        VENDOR_OUI_MAP.put("Netgear", "00:09:5B");
        VENDOR_OUI_MAP.put("Ubiquiti", "04:18:D6");
        VENDOR_OUI_MAP.put("Fortinet", "00:09:0F");
        VENDOR_OUI_MAP.put("Juniper", "00:05:85");
        VENDOR_OUI_MAP.put("H3C", "00:E0:FC");
        VENDOR_OUI_MAP.put("ZTE", "70:72:3C");
        VENDOR_OUI_MAP.put("Tenda", "C8:3A:35");
        VENDOR_OUI_MAP.put("Ruijie", "00:30:F1");
    }
    
    // 设备名称模板
    private static final String[] DEVICE_NAME_PREFIXES = {
        "SW", "RT", "AP", "FW", "LB", "SRV", "PC", "NB", "TAB", "PH"
    };
    
    private static final String[] LOCATION_SUFFIXES = {
        "BJ", "SH", "GZ", "SZ", "HZ", "NJ", "WH", "CD", "XA", "TJ"
    };
    
    public NetworkDeviceGenerator() {
        this(DeviceType.MAC_ADDRESS, DeviceFormat.STANDARD, true);
    }
    
    public NetworkDeviceGenerator(DeviceType deviceType, DeviceFormat format, boolean includeVendorInfo) {
        this.deviceType = deviceType;
        this.format = format;
        this.includeVendorInfo = includeVendorInfo;
    }
    
    @Override
    public String generate(GenerationContext context) {
        switch (deviceType) {
            case MAC_ADDRESS:
                return generateMacAddress();
            case DEVICE_ID:
                return generateDeviceId();
            case SERIAL_NUMBER:
                return generateSerialNumber();
            case HOSTNAME:
                return generateHostname();
            case INTERFACE_NAME:
                return generateInterfaceName();
            case VLAN_ID:
                return generateVlanId();
            case SSID:
                return generateSSID();
            case BSSID:
                return generateBSSID();
            case DEVICE_NAME:
                return generateDeviceName();
            default:
                return generateMacAddress();
        }
    }
    
    /**
     * 生成MAC地址
     */
    private String generateMacAddress() {
        StringBuilder mac = new StringBuilder();
        
        if (includeVendorInfo && random.nextBoolean()) {
            // 使用真实厂商OUI前缀
            String[] vendors = VENDOR_OUI_MAP.keySet().toArray(new String[0]);
            String vendor = vendors[random.nextInt(vendors.length)];
            String oui = VENDOR_OUI_MAP.get(vendor);
            
            switch (format) {
                case WINDOWS:
                    mac.append(oui.replace(":", "-"));
                    break;
                case CISCO:
                    mac.append(oui.replace(":", ".").toLowerCase());
                    break;
                case LINUX:
                default:
                    mac.append(oui.toLowerCase());
                    break;
            }
            
            // 生成后3字节
            for (int i = 0; i < 3; i++) {
                String separator = getSeparator();
                mac.append(separator);
                mac.append(String.format("%02x", random.nextInt(256)));
            }
        } else {
            // 生成随机MAC地址
            for (int i = 0; i < 6; i++) {
                if (i > 0) {
                    mac.append(getSeparator());
                }
                mac.append(String.format("%02x", random.nextInt(256)));
            }
        }
        
        return format == DeviceFormat.CISCO ? 
            formatCiscoMac(mac.toString()) : mac.toString();
    }
    
    /**
     * 获取MAC地址分隔符
     */
    private String getSeparator() {
        switch (format) {
            case WINDOWS:
                return "-";
            case CISCO:
                return ".";
            case LINUX:
            case STANDARD:
            default:
                return ":";
        }
    }
    
    /**
     * 格式化Cisco MAC地址 (xxxx.xxxx.xxxx)
     */
    private String formatCiscoMac(String mac) {
        String cleanMac = mac.replaceAll("[:-]", "");
        return cleanMac.substring(0, 4) + "." + 
               cleanMac.substring(4, 8) + "." + 
               cleanMac.substring(8, 12);
    }
    
    /**
     * 生成设备ID
     */
    private String generateDeviceId() {
        StringBuilder deviceId = new StringBuilder();
        
        // 添加前缀
        String[] prefixes = {"DEV", "NET", "SYS", "HW", "EQ"};
        deviceId.append(prefixes[random.nextInt(prefixes.length)]);
        
        // 添加年份
        deviceId.append(2020 + random.nextInt(5));
        
        // 添加随机数字
        deviceId.append(String.format("%06d", random.nextInt(1000000)));
        
        return deviceId.toString();
    }
    
    /**
     * 生成设备序列号
     */
    private String generateSerialNumber() {
        StringBuilder serial = new StringBuilder();
        
        switch (format) {
            case CISCO:
                // Cisco格式: FCW2xxxAxxxx
                serial.append("FCW");
                serial.append(2010 + random.nextInt(15));
                serial.append("A");
                serial.append(String.format("%04d", random.nextInt(10000)));
                break;
                
            case HUAWEI:
                // 华为格式: 2102xxxxxxxxxxxxx
                serial.append("2102");
                for (int i = 0; i < 13; i++) {
                    serial.append(random.nextInt(10));
                }
                break;
                
            case WINDOWS:
                // Dell格式: 7字符
                for (int i = 0; i < 7; i++) {
                    if (random.nextBoolean()) {
                        serial.append((char)('A' + random.nextInt(26)));
                    } else {
                        serial.append(random.nextInt(10));
                    }
                }
                break;
                
            default:
                // 通用格式
                for (int i = 0; i < 10; i++) {
                    if (random.nextBoolean()) {
                        serial.append((char)('A' + random.nextInt(26)));
                    } else {
                        serial.append(random.nextInt(10));
                    }
                }
                break;
        }
        
        return serial.toString();
    }
    
    /**
     * 生成主机名
     */
    private String generateHostname() {
        StringBuilder hostname = new StringBuilder();
        
        // 设备类型前缀
        String prefix = DEVICE_NAME_PREFIXES[random.nextInt(DEVICE_NAME_PREFIXES.length)];
        hostname.append(prefix);
        
        // 位置后缀
        String location = LOCATION_SUFFIXES[random.nextInt(LOCATION_SUFFIXES.length)];
        hostname.append("-").append(location);
        
        // 楼层或区域
        hostname.append("-").append("F").append(random.nextInt(50) + 1);
        
        // 设备编号
        hostname.append("-").append(String.format("%03d", random.nextInt(1000)));
        
        return hostname.toString().toLowerCase();
    }
    
    /**
     * 生成接口名称
     */
    private String generateInterfaceName() {
        switch (format) {
            case CISCO:
                String[] ciscoInterfaces = {
                    "GigabitEthernet0/0/", "FastEthernet0/", "Serial0/0/",
                    "Ethernet0/", "Vlan", "Loopback", "Tunnel"
                };
                String ciscoIface = ciscoInterfaces[random.nextInt(ciscoInterfaces.length)];
                if (ciscoIface.equals("Vlan") || ciscoIface.equals("Loopback") || ciscoIface.equals("Tunnel")) {
                    return ciscoIface + random.nextInt(1000);
                } else {
                    return ciscoIface + random.nextInt(48);
                }
                
            case HUAWEI:
                String[] huaweiInterfaces = {
                    "GigabitEthernet0/0/", "Ethernet0/0/", "Vlanif", "LoopBack", "Tunnel"
                };
                String huaweiIface = huaweiInterfaces[random.nextInt(huaweiInterfaces.length)];
                if (huaweiIface.equals("Vlanif") || huaweiIface.equals("LoopBack") || huaweiIface.equals("Tunnel")) {
                    return huaweiIface + random.nextInt(1000);
                } else {
                    return huaweiIface + random.nextInt(48);
                }
                
            case LINUX:
                String[] linuxInterfaces = {"eth", "wlan", "lo", "br", "veth", "docker"};
                String linuxIface = linuxInterfaces[random.nextInt(linuxInterfaces.length)];
                return linuxIface + random.nextInt(10);
                
            case WINDOWS:
                return "Local Area Connection " + (random.nextInt(10) + 1);
                
            default:
                return "interface" + random.nextInt(100);
        }
    }
    
    /**
     * 生成VLAN ID
     */
    private String generateVlanId() {
        // VLAN ID范围: 1-4094
        int vlanId = random.nextInt(4094) + 1;
        return String.valueOf(vlanId);
    }
    
    /**
     * 生成WiFi SSID
     */
    private String generateSSID() {
        String[] ssidPrefixes = {
            "WiFi", "Network", "Home", "Office", "Guest", "Public",
            "TP-LINK", "HUAWEI", "Xiaomi", "ASUS", "NETGEAR"
        };
        
        String prefix = ssidPrefixes[random.nextInt(ssidPrefixes.length)];
        
        if (random.nextBoolean()) {
            // 添加数字后缀
            return prefix + "_" + (random.nextInt(9999) + 1);
        } else {
            // 添加字母后缀
            char suffix = (char)('A' + random.nextInt(26));
            return prefix + "_" + suffix;
        }
    }
    
    /**
     * 生成WiFi BSSID (基本就是MAC地址格式)
     */
    private String generateBSSID() {
        return generateMacAddress();
    }
    
    /**
     * 生成设备名称
     */
    private String generateDeviceName() {
        String[] deviceTypes = {
            "Switch", "Router", "AccessPoint", "Firewall", "Server", 
            "Workstation", "Printer", "Camera", "Phone", "Tablet"
        };
        
        String[] brands = {
            "Cisco", "Huawei", "H3C", "Juniper", "Aruba", "Fortinet",
            "Dell", "HP", "Lenovo", "ASUS", "TP-Link", "Netgear"
        };
        
        String deviceType = deviceTypes[random.nextInt(deviceTypes.length)];
        String brand = brands[random.nextInt(brands.length)];
        String model = String.format("%04d", random.nextInt(10000));
        
        return brand + " " + deviceType + " " + model;
    }
    
    /**
     * 验证MAC地址格式
     */
    public static boolean isValidMacAddress(String mac) {
        if (mac == null) return false;
        
        // 支持多种格式
        String[] patterns = {
            "^([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}$",  // xx:xx:xx:xx:xx:xx 或 xx-xx-xx-xx-xx-xx
            "^([0-9A-Fa-f]{4}\\.){2}[0-9A-Fa-f]{4}$"    // xxxx.xxxx.xxxx (Cisco)
        };
        
        for (String pattern : patterns) {
            if (mac.matches(pattern)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 获取MAC地址厂商信息
     */
    public static String getVendorFromMac(String mac) {
        if (!isValidMacAddress(mac)) {
            return "Unknown";
        }
        
        // 提取OUI前缀 (前3字节)
        String cleanMac = mac.replaceAll("[:-.]", "").toUpperCase();
        String oui = cleanMac.substring(0, 6);
        String formattedOui = oui.substring(0, 2) + ":" + oui.substring(2, 4) + ":" + oui.substring(4, 6);
        
        for (Map.Entry<String, String> entry : VENDOR_OUI_MAP.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(formattedOui)) {
                return entry.getKey();
            }
        }
        
        return "Unknown";
    }
    
    /**
     * 生成网络配置信息
     */
    public String generateNetworkConfig() {
        StringBuilder config = new StringBuilder();
        
        config.append("Device: ").append(generateDeviceName()).append("\n");
        config.append("MAC: ").append(generateMacAddress()).append("\n");
        config.append("Serial: ").append(generateSerialNumber()).append("\n");
        config.append("Hostname: ").append(generateHostname()).append("\n");
        config.append("Interface: ").append(generateInterfaceName()).append("\n");
        config.append("VLAN: ").append(generateVlanId()).append("\n");
        
        return config.toString();
    }
}