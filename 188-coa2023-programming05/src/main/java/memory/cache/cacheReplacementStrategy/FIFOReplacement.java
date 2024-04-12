package memory.cache.cacheReplacementStrategy;


import memory.Memory;
import memory.cache.Cache;

/**
 * TODO 先进先出算法
 */
public class FIFOReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        //nothing to do
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
            if (cache.getTimeStamp(i) < cache.getTimeStamp(count)){
                count = i;
            }
        }
        if (Cache.isWriteBack){
            if (cache.isDirty(count) && cache.isValid(count)){
                Memory.getMemory().write(cache.getPAddr(count),Cache.LINE_SIZE_B,cache.getData(count));
            }
        }
        cache.update(count,addrTag,input);
        cache.setTimeStampFIFO(count);
        return count;
    }

}
