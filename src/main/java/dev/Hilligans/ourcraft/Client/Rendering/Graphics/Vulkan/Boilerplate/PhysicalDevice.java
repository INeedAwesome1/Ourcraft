package dev.Hilligans.ourcraft.Client.Rendering.Graphics.Vulkan.Boilerplate;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

public class PhysicalDevice {

    public VkPhysicalDevice physicalDevice;
    public VulkanInstance vulkanInstance;
    public VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.calloc();
    public VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc();
    public ArrayList<QueueFamily> queueFamilies = new ArrayList<>();
    public VkSurfaceCapabilitiesKHR surfaceCapabilities;
    public IntBuffer presentModes;
    public VkSurfaceFormatKHR.Buffer surfaceFormats;
    public LogicalDevice logicalDevice;
    public String deviceName;

    public PhysicalDevice(VkPhysicalDevice physicalDevice, VulkanInstance vulkanInstance) {
        this.physicalDevice = physicalDevice;
        this.vulkanInstance = vulkanInstance;
        getQueueFamilies();
        this.logicalDevice = createDevice();
        getSurfaceCapabilities();

        for(QueueFamily queueFamily : queueFamilies) {
            queueFamily.testPresent();
        }
    }

    public LogicalDevice createDevice() {
        return new LogicalDevice(this);
    }

    public boolean supportsSwapChain() {
        try(MemoryStack memoryStack = MemoryStack.stackPush()) {
            IntBuffer size = memoryStack.mallocInt(1);
            vkEnumerateDeviceExtensionProperties(physicalDevice, (String) null, size, null);
            VkExtensionProperties.Buffer device_extensions = VkExtensionProperties.mallocStack(size.get(0), memoryStack);
            vkEnumerateDeviceExtensionProperties(physicalDevice, (String) null, size, device_extensions);
            for (int x = 0; x < device_extensions.capacity(); x++) {
                if (device_extensions.get(x).extensionNameString().equals(VK_KHR_SWAPCHAIN_EXTENSION_NAME)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDiscreteGPU() {
        return properties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU;
    }

    public boolean isIntegratedGPU() {
        return properties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU;
    }

    public boolean isVirtualGPU() {
        return properties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU;
    }

    private void getQueueFamilies() {
        try(MemoryStack memoryStack = MemoryStack.stackPush()) {
            vkGetPhysicalDeviceProperties(physicalDevice, properties);
            IntBuffer size = memoryStack.mallocInt(1);
            vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, size, null);
            VkQueueFamilyProperties.Buffer buffer = VkQueueFamilyProperties.calloc(size.get(0));
            vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, size, buffer);
            AtomicInteger x = new AtomicInteger();
            buffer.forEach(t -> queueFamilies.add(new QueueFamily(t, this, x.getAndIncrement())));
        }
    }

    private void getSurfaceCapabilities() {
        try(MemoryStack memoryStack = MemoryStack.stackPush()) {
            surfaceCapabilities = VkSurfaceCapabilitiesKHR.calloc();
            vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, logicalDevice.defaultVulkanWindow.surface, surfaceCapabilities);

            IntBuffer size = memoryStack.mallocInt(1);
            vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, logicalDevice.getDefaultWindow().surface, size, null);
            surfaceFormats = VkSurfaceFormatKHR.calloc(size.get(0));
            vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, logicalDevice.getDefaultWindow().surface, size, surfaceFormats);

            vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, logicalDevice.getDefaultWindow().surface, size, null);
            presentModes = memoryStack.callocInt(size.get(0));
            vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, logicalDevice.getDefaultWindow().surface, size, presentModes);
        }
    }

    public VkSurfaceFormatKHR chooseFormat(int idealFormat, int idealColorSpace) {
        for(VkSurfaceFormatKHR vkSurfaceFormatKHR : surfaceFormats) {
            if(vkSurfaceFormatKHR.format() == idealFormat && vkSurfaceFormatKHR.colorSpace() == idealColorSpace) {
                return vkSurfaceFormatKHR;
            }
        }
        return surfaceFormats.get(0);
    }

    public int chooseSwapPresentMode(int idealMode) {
        for(int x = 0; x < presentModes.capacity(); x++) {
            if(presentModes.get(x) == idealMode) {
                return idealMode;
            }
        }
        return VK_PRESENT_MODE_FIFO_KHR;
    }

    public void cleanup() {
        deviceFeatures.free();
        properties.free();
        surfaceCapabilities.free();
    }

}
