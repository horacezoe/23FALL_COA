package memory.disk;

import java.util.Arrays;

public class Scheduler {

    /**
     * 先来先服务算法
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double FCFS(int start, int[] request) {
        double sum = 0;
        for (int i : request) {
            sum += Math.abs(i - start);
            start = i;
        }
        return sum / request.length;
    }

    /**
     * 最短寻道时间优先算法
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double SSTF(int start, int[] request) {
        double sum = 0;
        boolean[] visited = new boolean[request.length];
        for (int i = 0; i < request.length; i++) {
            int min = Disk.TRACK_NUM;
            int minIndex = -1;
            for (int j = 0; j < request.length; j++) {
                if (Math.abs(request[j] - start) < min && !visited[j]) {
                    min = Math.abs(request[j] - start);
                    minIndex = j;
                }
            }
            sum += min;
            start = request[minIndex];
            visited[minIndex] = true;
        }
        return sum / request.length;
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
        double sum = 0;
        Arrays.sort(request);
        if (direction) {
            if (start <= request[0]) {
                sum = request[request.length - 1] - start;
            } else {
                sum = Disk.TRACK_NUM - 1 - start + Disk.TRACK_NUM - 1 - request[0];
            }
        } else {
            if (start >= request[request.length - 1]) {
                sum = start - request[0];
            } else {
                sum = start + request[request.length - 1];
            }
        }
        return sum / request.length;
    }

    /**
     * C-SCAN算法：默认磁头向磁道号增大方向移动
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @return 平均寻道长度
     */
    public double CSCAN(int start,int[] request){
        double sum = 0;
        int lower_bound = -1,max = 0;
        for(int i: request){
            if(i > max){
                max = i;
            }
            if(i < start && i > lower_bound){
                lower_bound = i;
            }
        }
        if(lower_bound < 0) {
            sum = max - start;
        }else{
            sum = Disk.TRACK_NUM - 1 - start + Disk.TRACK_NUM - 1 + lower_bound;
        }
        return sum/ request.length;
    }

    /**
     * LOOK算法
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @param direction 磁头初始移动方向，true表示磁道号增大的方向，false表示磁道号减小的方向
     * @return 平均寻道长度
     */
    public double LOOK(int start,int[] request,boolean direction){
        double sum = 0;
        Arrays.sort(request);
        if(start <= request[0]){
            sum = request[request.length - 1] - start;
        }else if(start >= request[request.length - 1]){
            sum = start - request[0];
        }else{
            if(direction){
                sum = request[request.length - 1] - start + request[request.length - 1] - request[0];
            }else{
                sum = start - request[0] + request[request.length - 1] - request[0];
            }
        }
        return sum / request.length;
    }

    /**
     * C-LOOK算法：默认磁头向磁道号增大方向移动
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @return 平均寻道长度
     */
    public double CLOOK(int start,int[] request){
        double sum = 0;
        int lower_bound = -1,max = 0,min = Disk.TRACK_NUM;
        for(int i: request){
            if(i > max){
                max = i;
            }
            if(i < min){
                min = i;
            }
            if(i < start && i > lower_bound){
                lower_bound = i;
            }
        }
        if(lower_bound < 0){
            sum = request[request.length - 1] - start;
        }else if(start >= max){
            sum = start - min + max - min;
        }else{
            sum = max - start + max - min + lower_bound - min;
        }
        return sum / request.length;
    }

}
