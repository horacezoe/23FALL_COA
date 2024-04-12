package memory.disk;


public class Scheduler {

    /**
     * 先来先服务算法
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double FCFS(int start, int[] request) {
        int length = 0;
        int request_size = request.length;
        for (int i = 0; i < request_size; i++) {
            length = length + abs(request[i] - start);
            start = request[i];
        }
        return (length / (request_size * 1.0));
    }

    /**
     * 最短寻道时间优先算法
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double SSTF(int start, int[] request) {
        int length = 0;
        int request_size = request.length;
        int count_order = 1;
        while (count_order <= request_size) {
            int count = 0;
            for (int i = 0; i < request.length; i++) {
                if (abs(start - request[count]) > abs(start - request[i])) {
                    count = i;
                }
            }
            length = length + abs(start - request[count]);
            start = request[count];
            request[count] = Integer.MAX_VALUE;
            count_order++;
        }
        return (length / (request_size * 1.0));
    }

    /**
     * 扫描算法
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @param direction 磁头初始移动方向，true表示磁道号增大的方向，false表示磁道号减小的方向
     * @return 平均寻道长度
     */
    public double SCAN(int start, int[] request, boolean direction) {
        int length = 0;
        int request_size = request.length;
        int min = 256;
        int max = -1;
        int LargestMin = -1;//最大的小于start的
        int LeastMax = 256;//最小的大于start的
        for(int i = 0; i < request_size;i++){
            if(request[i] < min){
                min = request[i];
            }
            if(request[i] > max){
                max = request[i];
            }
            if(request[i] < start && request[i] > LargestMin){
                LargestMin = request[i];
            }
            if(request[i] > start && request[i] < LeastMax){
                LeastMax = request[i];
            }
        }

        if(direction){
            if(start <= min){
                length = length + abs(max - start);
            }else{
                length = length + abs(255 - start);
                length = length + abs(min - 255);
            }
        }else {
            if (start >= max) {
                length = length + abs(min - start);
            } else {
                length = length + start;
                length = length +  max;
            }
        }
            return (length / (request_size * 1.0));
        }
//    public double SCAN(int start, int[] request, boolean direction) {
//        int length = 0;
//        int request_size = request.length;
//        int temp = 1;
//        while (temp <= request_size + 1) {
//            boolean isFinished = true;
//            for (int i = 0; i < request_size; i++) {
//                if (request[i] != -1) {
//                    isFinished = false;
//                    break;
//                }
//            }
//            if (isFinished) {
//                break;
//            }
//            if (direction) {
//                int min = 256;
//                int count = -1;
//                for (int i = 0; i < request_size; i++) {
//                    if (request[i] == -1) {
//                        continue;
//                    }
//                    if (request[i] > start && request[i] < min) {
//                        count = i;
//                        min = request[i];
//                    }
//                }
//                if (count == -1) {
//                    length = length + abs(255 - start);
//                    start = 255;//如果start就在255怎么办
//                    direction = !direction;
//                } else {
//                    length = length + abs(request[count] - start);
//                    start = request[count];
//                    request[count] = -1;
//                }
//            } else {
//                int max = -1;
//                int count = -1;
//                for (int i = 0; i < request_size; i++) {
//                    if (request[i] == -1) {
//                        continue;
//                    }
//                    if (request[i] < start && request[i] > max) {
//                        count = i;
//                        max = request[i];
//                    }
//                }
//                if (count == -1) {
//                    length = length + abs(0 - start);
//                    start = 0;
//                    direction = !direction;
//                } else {
//                    length = length + abs(request[count] - start);
//                    start = request[count];
//                    request[count] = -1;
//                }
//            }
//            temp++;
//        }
//        return (length / (request_size * 1.0));
//    }

    /**
     * C-SCAN算法：默认磁头向磁道号增大方向移动
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */

    public double CSCAN(int start, int[] request) {
        int length = 0;
        int request_size = request.length;
        int max = -1;
        int min = 256;
        for(int i = 0; i < request_size;i++){
            if(request[i] < min){
                min = request[i];
            }
            if(request[i] > max){
                max = request[i];
            }
        }
        if(start <= min){
            length = length + abs(max - start);
        }else{
            int LM = -1;
            for(int i = 0;i < request_size;i++){
                if(request[i] < start && request[i] > LM){
                    LM = request[i];
                }
            }
            length = length + abs(255 - start);
            length = length + abs(LM) + 255;
        }
        return (length / (request_size * 1.0));
    }
//    public double CSCAN(int start, int[] request) {
//        int length = 0;
//        int request_size = request.length;
//        int temp = 1;
//        while (temp <= request_size + 1) {
//            boolean isFinished = true;
//            for (int i = 0; i < request_size; i++) {
//                if (request[i] != -1) {
//                    isFinished = false;
//                    break;
//                }
//            }
//            if (isFinished) {
//                break;
//            }
//            int min = 256;
//            int count = -1;
//            for (int i = 0; i < request_size; i++) {
//                if (request[i] == -1) {
//                    continue;
//                }
//                if (request[i] > start && request[i] < min) {
//                    count = i;
//                    min = request[i];
//                }
//            }
//            if (count == -1) {
//                length = length + abs(255 - start) + 255;
//                start = 0;
//            } else {
//                length = length + abs(request[count] - start);
//                start = request[count];
//                request[count] = -1;
//            }
//            temp++;
//        }
//        return (length / (request_size * 1.0));
//    }

    /**
     * LOOK算法
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @param direction 磁头初始移动方向，true表示磁道号增大的方向，false表示磁道号减小的方向
     * @return 平均寻道长度
     */
    public double LOOK(int start, int[] request, boolean direction) {
        int length = 0;
        int request_size = request.length;
        int temp = 1;
        while (temp <= request_size + 1) {
            boolean isFinished = true;
            for (int i = 0; i < request_size; i++) {
                if (request[i] != -1) {
                    isFinished = false;
                    break;
                }
            }
            if (isFinished) {
                break;
            }
            if (direction) {
                int min = 256;
                int count = -1;
                for (int i = 0; i < request_size; i++) {
                    if (request[i] == -1) {
                        continue;
                    }
                    if (request[i] > start && request[i] < min) {
                        count = i;
                        min = request[i];
                    }
                }
                if (count == -1) {
                    direction = !direction;
                } else {
                    length = length + abs(request[count] - start);
                    start = request[count];
                    request[count] = -1;
                }
            } else {
                int max = -1;
                int count = -1;
                for (int i = 0; i < request_size; i++) {
                    if (request[i] == -1) {
                        continue;
                    }
                    if (request[i] < start && request[i] > max) {
                        count = i;
                        max = request[i];
                    }
                }
                if (count == -1) {
                    direction = !direction;
                } else {
                    length = length + abs(request[count] - start);
                    start = request[count];
                    request[count] = -1;
                }
            }
            temp++;
        }
        return (length / (request_size * 1.0));
    }

    /**
     * C-LOOK算法：默认磁头向磁道号增大方向移动
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double CLOOK(int start, int[] request) {
        int length = 0;
        int request_size = request.length;
        int min = 256;
        int max = -1;
        for(int i = 0; i < request_size;i++){
            if(request[i] < min){
                min = request[i];
            }
            if(request[i] > max){
                max = request[i];
            }
        }
        if(start <= min){
            length = length + abs(max - start);
        }else if(start > max){
            length = length + start + max - 2 * min;
        }else{
            int LM = -1;
            for(int i = 0;i < request_size;i++){
                if(request[i] < start && request[i] > LM){
                    LM = request[i];
                }
            }
            length = length + abs(max - start) + abs(max - min) + abs(LM - min);
        }
        return (length / (request_size * 1.0));
    }
//    public double CLOOK(int start, int[] request) {
//        int length = 0;
//        int request_size = request.length;
//        int temp = 1;
////        int min_value = 256;
////        for (int i = 0;i < request_size ; i++){
////            if (request[i] < min_value){
////                min_value = request[i];
////            }
////        }
//        while (temp <= request_size + 1) {
//            boolean isFinished = true;
//            for (int i = 0; i < request_size; i++) {
//                if (request[i] != -1) {
//                    isFinished = false;
//                    break;
//                }
//            }
//            if (isFinished) {
//                break;
//            }
//            int min_value = 256;
//            for (int i = 0; i < request_size; i++) {
//                if (request[i] == -1) {
//                    continue;
//                }
//                if (request[i] < min_value) {
//                    min_value = request[i];
//                }
//            }
//            int min = 256;
//            int count = -1;
//            for (int i = 0; i < request_size; i++) {
//                if (request[i] == -1) {
//                    continue;
//                }
//                if (request[i] > start && request[i] < min) {
//                    count = i;
//                    min = request[i];
//                }
//            }
//            if (count == -1) {
//                length = length + abs(start - min_value);
//                start = min_value;
//            } else {
//                length = length + abs(request[count] - start);
//                start = request[count];
//                request[count] = -1;
//            }
//            temp++;
//        }
//        return (length / (request_size * 1.0));
//    }

    public int abs(int temp) {
        if (temp < 0) {
            return -temp;
        } else {
            return temp;
        }
    }

}
