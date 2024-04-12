package memory.cache.cacheReplacementStrategy;

import memory.Memory;
import memory.cache.Cache;

/**
 * TODO 最近最少用算法
 */
public class LRUReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        Cache cache = Cache.getCache();
//        int SETS = cache.getSETS();
        int setSize = cache.getSetSize();
        int group_num = rowNO / setSize;
        for(int i = group_num * setSize; i < group_num * setSize + setSize; i++){
            if(i == rowNO){
                cache.setTimeStampZero(i);
            }else{
                cache.setTimeStamp(i);
            }
        }
    }

    @Override
    public int replace(int start, int end, char[] addrTag, byte[] input) {
        Cache cache = Cache.getCache();
        int count = start;
        for (int i = start; i < end; i++){
            if (!cache.isValid(i)) {
                count = i;
                break;
            }
            if (cache.getTimeStamp(i) > cache.getTimeStamp(count)){
                count = i;
            }
        }
        if (Cache.isWriteBack){
            if (cache.isDirty(count) && cache.isValid(count)){
                Memory.getMemory().write(cache.getPAddr(count),Cache.LINE_SIZE_B,cache.getData(count));
            }
        }
        cache.update(count,addrTag,input);
        for (int i = start;i < end; i++){
            if (cache.isValid(i) && i != count){
                cache.setTimeStamp(i);
            }
        }
        return count;
    }

}





























