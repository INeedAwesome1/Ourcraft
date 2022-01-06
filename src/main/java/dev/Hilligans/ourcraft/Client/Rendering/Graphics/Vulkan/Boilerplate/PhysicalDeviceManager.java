package dev.Hilligans.ourcraft.Client.Rendering.Graphics.Vulkan.Boilerplate;

import dev.Hilligans.ourcraft.Client.Rendering.Graphics.Vulkan.VulkanEngineException;
import dev.Hilligans.ourcraft.Util.ArgumentContainer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPhysicalDevice;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices;

public class PhysicalDeviceManager {

    public ArrayList<PhysicalDevice> devices = new ArrayList<>();
    public VulkanInstance vulkanInstance;
    public int defaultDevice;
    public String defaultDeviceName;

    public PhysicalDeviceManager(VulkanInstance vulkanInstance) {
        this.vulkanInstance = vulkanInstance;
        defaultDevice = vulkanInstance.getArgumentContainer().getInt("--vkDevice", 0);
        defaultDeviceName = vulkanInstance.getArgumentContainer().getString("--vkDeviceName", "");
    }

    public PhysicalDevice getDefaultDevice() {
        if(!defaultDeviceName.equals("")) {
            getDevice(defaultDeviceName);
        }
        return getDevice(defaultDevice);
    }

    public PhysicalDevice getDevice(int index) {
        if(index >= devices.size()) {
            throw new VulkanEngineException("Unknown vulkan physical device: " + index);
        }
        return devices.get(index);
    }

    public PhysicalDevice getDevice(String name) {
        for(PhysicalDevice device : devices) {
            if(defaultDeviceName.equals(device.deviceName)) {
                return device;
            }
        }
        throw new VulkanEngineException("Unknown vulkan physical device: " + name);
    }

    public PhysicalDevice selectPhysicalDevice() {
        try(MemoryStack memoryStack = MemoryStack.stackPush()) {
            IntBuffer deviceCount = memoryStack.mallocInt(1);
            vkEnumeratePhysicalDevices(vulkanInstance.vkInstance, deviceCount, null);
            PointerBuffer buffer = memoryStack.mallocPointer(deviceCount.get(0));
            vkEnumeratePhysicalDevices(vulkanInstance.vkInstance, deviceCount, buffer);
            int size = deviceCount.get(0);
            for (int x = 0; x < size; x++) {
                PhysicalDevice device = new PhysicalDevice(new VkPhysicalDevice(buffer.get(x), vulkanInstance.vkInstance), vulkanInstance);
                device.supportsSwapChain();
                devices.add(device);
            }
        }
        return getDefaultDevice();
    }
}
