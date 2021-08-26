#include "includes/utils.h"

std::string readStringFromLogFile(std::ifstream &input_stream, const std::string& log_path){
    //assert(log_path.empty() && "isLogPathEmpty");
    input_stream.open(log_path, std::ios::in);
    std::stringstream buffer;
    buffer << input_stream.rdbuf();
    input_stream.close();
    return buffer.str();
}