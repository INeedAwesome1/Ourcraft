package dev.Hilligans.ourcraft.Client.Rendering.Graphics.Vulkan.Boilerplate;

import dev.Hilligans.ourcraft.Client.Rendering.Graphics.Vulkan.Boilerplate.Window.*;
import dev.Hilligans.ourcraft.Client.Rendering.Graphics.Vulkan.VulkanEngineException;
import dev.Hilligans.ourcraft.Util.ArgumentContainer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK11.*;

public class VulkanInstance {

    public PhysicalDevice physicalDevice;
    public PhysicalDeviceManager devices;
    public LogicalDevice logicalDevice;
    public VkInstance vkInstance;
    public VkApplicationInfo applicationInfo;
    public VkInstanceCreateInfo createInfo;
    public VulkanWindow vulkanWindow;
    public VulkanProperties vulkanProperties;

    public PointerBuffer extensions = memCallocPointer(64);
    public PointerBuffer instance = memAllocPointer(1);

    public VulkanInstance(VulkanProperties vulkanProperties) {
        this.vulkanProperties = vulkanProperties;
            createInstance();
            devices = new PhysicalDeviceManager(this);
            physicalDevice = devices.selectPhysicalDevice();
            logicalDevice = physicalDevice.logicalDevice;
            vulkanWindow = physicalDevice.logicalDevice.getDefaultWindow();
            vulkanWindow.selectFamily();
            vulkanWindow.graphicsFamily.getQueue(0);
            vulkanWindow.addData();

    }

    public void createInstance() {
        try(MemoryStack memoryStack = MemoryStack.stackPush()) {
            if(vulkanProperties.initializeWindow) {
                glfwInit();
                PointerBuffer glfwExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
                if (glfwExtensions == null) {
                    exit("Unable to initialize vulkan");
                    return;
                }
                for (int x = 0; x < glfwExtensions.capacity(); x++) {
                    extensions.put(glfwExtensions.get(x));
                }
            }
            PointerBuffer validationLayers = null;
            long pAddress = NULL;
            if (vulkanProperties.enableLayer) {
                validationLayers = vulkanProperties.enableLayers(memoryStack);
                pAddress = vulkanProperties.setupLayerDebugger().address();
                extensions.put(memoryStack.UTF8(VK_EXT_DEBUG_UTILS_EXTENSION_NAME));

            }

            applicationInfo = VkApplicationInfo.calloc();
            applicationInfo.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
            applicationInfo.pNext(NULL);
            applicationInfo.pApplicationName(memoryStack.UTF8("test tri"));
            applicationInfo.applicationVersion(VK_MAKE_VERSION(1, 0, 0));
            applicationInfo.pEngineName(memoryStack.UTF8("No Engine"));
            applicationInfo.engineVersion(VK_MAKE_VERSION(1, 0, 0));
            applicationInfo.apiVersion(VK_API_VERSION_1_1);

            extensions.flip();

            createInfo = VkInstanceCreateInfo.calloc();
            createInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
            createInfo.pApplicationInfo(applicationInfo);
            createInfo.ppEnabledExtensionNames(extensions);
            createInfo.pNext(pAddress);
            createInfo.ppEnabledLayerNames(validationLayers);

            if (vkCreateInstance(createInfo, null, instance) != VK_SUCCESS) {
                exit("Failed to create vulkan instance");
            }

            vkInstance = new VkInstance(instance.get(0), createInfo);
        }
    }

    public void run() {
        //vulkanWindow.startDrawing();
        //cleanup();
    }

    public static final int MAX_FRAMES_IN_FLIGHT = 2;

    public void selectPhysicalDevice() {
        try(MemoryStack memoryStack = MemoryStack.stackPush()) {
            IntBuffer deviceCount = memoryStack.mallocInt(1);
            vkEnumeratePhysicalDevices(vkInstance, deviceCount, null);
            PointerBuffer buffer = memoryStack.mallocPointer(deviceCount.get(0));
            vkEnumeratePhysicalDevices(vkInstance, deviceCount, buffer);
            int size = deviceCount.get(0);
            ArrayList<PhysicalDevice> devices = new ArrayList<>(size);
            for (int x = 0; x < size; x++) {
                devices.add(new PhysicalDevice(new VkPhysicalDevice(buffer.get(x), vkInstance), this));
            }

            physicalDevice = devices.get(0);

            //TODO fix
            physicalDevice.supportsSwapChain();
        }
    }

    public PhysicalDevice getDefaultDevice() {
        return devices.getDefaultDevice();
    }

    public void cleanup() {
        logicalDevice.destroy();
        physicalDevice.cleanup();
        vkDestroyInstance(vkInstance,null);
        if(vulkanProperties.initializeWindow) {
            glfwTerminate();
        }
    }

    public ArgumentContainer getArgumentContainer() {
        return vulkanProperties.argumentContainer;
    }

    public void exit(String reason) {
        new VulkanEngineException(reason).printStackTrace();
        System.exit(0);
    }

}
