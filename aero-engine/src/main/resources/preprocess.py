import dask.dataframe as dd


def preprocess_file(file_path_from,file_path_to):
    # 指定列名和数据类型（根据实际情况进行修改）
    dtype = {'column1': float, 'column2': float, 'column3': float}

    # 逐块读取文件
    df = dd.read_csv(file_path_from, dtype=dtype, blocksize=10e6, error_bad_lines=False, skiprows=range(16), delimiter=',')

    # 将处理后的结果保存为新文件
    df.to_csv(file_path_to, index=False, single_file=True)

file_path_from = "F:\SanXiao\SanXiaoProData\ABD_300rpm_r12.csv"

file_path_to = "F:\SanXiao\SanXiaoProData\processed_file.csv"

preprocess_file(file_path_from,file_path_to)
