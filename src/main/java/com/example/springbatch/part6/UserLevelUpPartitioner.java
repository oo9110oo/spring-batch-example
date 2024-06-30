package com.example.springbatch.part6;

import com.example.springbatch.part4.UserREpository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class UserLevelUpPartitioner implements Partitioner {

    private UserREpository userREpository;

    public UserLevelUpPartitioner(UserREpository userREpository) {
        this.userREpository = userREpository;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        long minId = userREpository.findMinId();
        long maxId = userREpository.findMaxId();

        long targetSize = (maxId - minId) / gridSize +1;


        /**
         * partition0 : 1, 5000
         * partition1 : 5001, 10000
         * ...
         * partition7 : 35001, 40000
         */
        Map<String, ExecutionContext> result = new HashMap<>();

        long number = 0;    // step의 번호

        long start = minId;
        long end = start + targetSize - 1;

        while(start <= maxId) {
            ExecutionContext value = new ExecutionContext();

            result.put("partition" + number, value);

            if (end >= maxId) {
                end = maxId;
            }

            value.putLong("minId", start);
            value.putLong("maxId", end);

            start += targetSize;
            end += targetSize;
            number++;
        }
        return result;
    }
}
