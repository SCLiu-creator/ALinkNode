package superlink.testjava;

//import org.lwjgl.glfw.Callbacks;
//import org.lwjgl.glfw.GLFWErrorCallback;
//import org.lwjgl.opengl.GL;
//import org.lwjgl.opengles.GLES;
//import org.lwjgl.opengles.GLES30;
//import org.lwjgl.system.Configuration;
//import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
//
//import static org.lwjgl.glfw.GLFW.*;
//import static org.lwjgl.opengles.GLES20.*;
public class LgTest {


//        private static long window;
//        private static int program;
//        private static int vao;
//
//        private static void init() {
//            GLFWErrorCallback.createPrint(System.err).set();
//            if (!glfwInit())
//                throw new IllegalStateException("glfwInit failed");
//            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
//            glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);
//            window = glfwCreateWindow(512, 512, "LwjglDemo", 0, 0);
//            if (window == 0)
//                throw new IllegalStateException("glfwCreateWindow failed");
//            glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
//                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
//                    glfwSetWindowShouldClose(win, true);
//            });
//            glfwMakeContextCurrent(window);
//            glfwSwapInterval(1);
//
//            Configuration.OPENGLES_EXPLICIT_INIT.set(true);
//            GLES.create(GL.getFunctionProvider());
//            GLES.createCapabilities();
//            glClearColor(0.0f, 0.5f, 0.5f, 0.0f);
//
//            final int vs = glCreateShader(GL_VERTEX_SHADER);
//            glShaderSource(vs, "#version 300 es\n" +
//                    "layout (location = 0) in vec3 pos;\n" +
//                    "void main() {\n" +
//                    "    gl_Position = vec4(pos.x, pos.y, pos.z, 1.0);\n" +
//                    "}");
//            glCompileShader(vs);
//            final int[] ok = new int[1];
//            glGetShaderiv(vs, GL_COMPILE_STATUS, ok);
//            if (ok[0] == 0)
//                throw new IllegalStateException("glCompileShader(VS) failed: " + glGetShaderInfoLog(vs));
//            final int fs = glCreateShader(GL_FRAGMENT_SHADER);
//            glShaderSource(fs, "#version 300 es\n" +
//                    "precision mediump float;\n" +
//                    "out vec4 FragColor;\n" +
//                    "void main() {\n" +
//                    "    FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n" +
//                    "}");
//            glCompileShader(fs);
//            glGetShaderiv(fs, GL_COMPILE_STATUS, ok);
//            if (ok[0] == 0)
//                throw new IllegalStateException("glCompileShader(FS) failed: " + glGetShaderInfoLog(fs));
//            program = glCreateProgram();
//            glAttachShader(program, vs);
//            glAttachShader(program, fs);
//            glLinkProgram(program);
//            glGetShaderiv(program, GL_LINK_STATUS, ok);
//            if (ok[0] == 0)
//                throw new IllegalStateException("glLinkProgram() failed: " + glGetProgramInfoLog(program));
//            glDeleteShader(vs);
//            glDeleteShader(fs);
//
//            try (final MemoryStack stack = MemoryStack.stackPush()) {
//                final IntBuffer bo2 = stack.callocInt(2);
//                glGenBuffers(bo2);
//                vao = GLES30.glGenVertexArrays();
//                GLES30.glBindVertexArray(vao);
//                final int vbo = bo2.get(0);
//                glBindBuffer(GL_ARRAY_BUFFER, vbo);
//                glBufferData(GL_ARRAY_BUFFER, new float[]{
//                        +0.5f, +0.5f, 0.0f, // top right
//                        +0.5f, -0.5f, 0.0f, // bottom right
//                        -0.5f, -0.5f, 0.0f, // bottom left
//                        -0.5f, +0.5f, 0.0f, // top left
//                }, GL_STATIC_DRAW);
//                final int ebo = bo2.get(1);
//                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
//                glBufferData(GL_ELEMENT_ARRAY_BUFFER, new short[]{
//                        0, 3, 1, // triangle-1
//                        1, 3, 2, // triangle-2
//                }, GL_STATIC_DRAW);
//                glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
//                glEnableVertexAttribArray(0);
//                GLES30.glBindVertexArray(0);
//                glDeleteBuffers(bo2);
//            }
//        }
//
//        private static void exit() {
//            if (vao != 0) {
//                GLES30.glDeleteVertexArrays(vao);
//                vao = 0;
//            }
//            if (program != 0) {
//                glDeleteProgram(program);
//                program = 0;
//            }
//            if (window != 0) {
//                Callbacks.glfwFreeCallbacks(window);
//                glfwDestroyWindow(window);
//                window = 0;
//            }
//            glfwTerminate();
//            final GLFWErrorCallback cb = glfwSetErrorCallback(null);
//            if (cb != null)
//                cb.free();
//        }
//
//        private static void loop() {
//            while (!glfwWindowShouldClose(window)) {
//                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//
//                glUseProgram(program);
//                GLES30.glBindVertexArray(vao);
//                glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0);
//
//                glfwSwapBuffers(window);
//                glfwPollEvents();
//            }
//        }
//
//        public static void main(String[] args) {
//            try {
//                init();
//                loop();
//            } finally {
//                exit();
//            }
//        }
//    }
}
